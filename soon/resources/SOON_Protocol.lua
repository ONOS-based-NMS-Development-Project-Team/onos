-- -- @brief SOON Protocol dissector plugin based on WebSocket
-- -- @author yby
-- -- @date 208.09.19

do

    -- define split function
    local function split(inputstr, sep)
      if sep == nil then
        sep = "%s"
      end
      local t={} ; i=1
      for str in string.gmatch(inputstr, "([^"..sep.."]+)") do
        t[i] = str
        i = i + 1
      end
      return t
    end

    local p_websocket = Proto("SOON", "SOON Model Control")
    local thebool = { [0] = "False", [1] = "True" }
    local themask = { [0] = " ", [1] = " [MASKED]" }

    -- description table for Opcode
    local theopcode = {
        [0] = "continues",
        [1] = "WebSocket Text [FIN]",
        [2] = "WebSocket binary [FIN]",
        [3] = "reserved for further non-control frames",
        [4] = "reserved for further non-control frames",
        [5] = "reserved for further non-control frames",
        [6] = "reserved for further non-control frames",
        [7] = "reserved for further non-control frames",
        [8] = "WebSocket Connection Close [FIN]",
        [9] = "WebSocket Ping [FIN]",
        [10] = "WebSocket Pong [FIN]",
        [11] = "reserved for further control frames",
        [12] = "reserved for further control frames",
        [13] = "reserved for further control frames",
        [14] = "reserved for further control frames",
        [15] = "reserved for further control frames",
    }

    local f_fin = ProtoField.uint8("WebSocket-Z.fin", "Fin", nil, thebool, 0x80)
    local f_reserved = ProtoField.uint8("WebSocket-Z.reserved", "Reserved", base.HEX, nil, 0x70)
    local f_opcode = ProtoField.uint8("WebSocket-Z.opcode", "Opcode", nil, theopcode, 0x0F)
    local f_mask = ProtoField.uint8("WebSocket-Z.mask", "Mask", nil, thebool, 0x80)
    local f_payloadlen = ProtoField.uint8("WebSocket-Z.payloadlen", "Payload length", base.DEC, nil, 0x7F)
    local f_extpayloadlen = ProtoField.uint16("WebSocket-Z.extpayloadlen", "Extended Payload length (16 bits)", base.DEC)
    local f_maskingkey = ProtoField.uint32("WebSocket-Z.maskingkey", "Masking-key", base.HEX)
    local f_payload = ProtoField.string("WebSocket-Z.Payload", "Payload")
    local soon_msgId = ProtoField.string("soon.msgId", "SOON Message ID")
    local soon_msgType = ProtoField.string("soon.msgType", "SOON Message Type")
    local soon_msgContent = ProtoField.string("soon.msgContent", "SOON Message Content")
    local is_soon_msg = ProtoField.string("soon.is_msg", "Whether it is SOON message")

    p_websocket.fields = { f_fin, f_reserved, f_opcode, f_mask, f_payloadlen, f_extpayloadlen, f_maskingkey, f_payload,
                            soon_msgId, soon_msgType, soon_msgContent }


    -- GetBits: Get some consecutive bits value from a WORD
    -- Param description:
    --     src: the source value from which we want to extract bit value
    --     sb:  startbit, the leftmost bit is refferred to as 0, the rightmost 15
    --     eb:  endbit, same as sb
    -- return value: the extracted bits value.
    local function GetBits(src, sb, eb)
        if src > 65535 or sb > 15 or eb > 15 or sb > eb then return 0 end
        local temp = src % (2 ^ (16 - sb))
        local tail = temp % (2 ^ (15 - eb))
        temp = temp - tail
        temp = temp / (2 ^ (15 - eb))
        return temp
    end


    -- decode the data string
    local bxor = bit.bxor
    local byte = string.byte
    local concat = table.concat
    local transformed = {}
    local function XORMask(data, mask)
        for i = 1, #data do
            transformed[i] = bxor(data[i], mask[(i - 1) % 4 + 1])
        end

        return transformed
    end


    --将16进制串转换为字符串
    function hex2str(hex)
        --判断输入类型
        if (type(hex) ~= "string") then
            return nil, "hex2str invalid input type"
        end
        --拼接字符串
        local index = 1
        local ret = ""
        for index = 1, hex:len() do
            ret = ret .. string.format("%02X", hex:sub(index):byte())
        end

        return ret
    end


    --将字符串按格式转为16进制串
    function str2hex(str)
        --判断输入类型
        if (type(str) ~= "string") then
            return nil, "str2hex invalid input type"
        end
        --滤掉分隔符
        str = str:gsub("[%s%p]", ""):upper()
        --检查内容是否合法
        if (str:find("[^0-9A-Fa-f]") ~= nil) then
            return nil, "str2hex invalid input content"
        end
        --检查字符串长度
        if (str:len() % 2 ~= 0) then
            return nil, "str2hex invalid input lenth"
        end
        --拼接字符串
        local index = 1
        local ret = ""
        for index = 1, str:len(), 2 do
            ret = ret .. string.char(tonumber(str:sub(index, index + 1), 16))
        end

        return ret
    end


    -- the websocket dissector function
    local function p_websocket_dissector(buffer, pkt, root)
        local buf_len = buffer:len()
        if buf_len < 2 then return false end

        local subtree = root:add(p_websocket, buffer())
        subtree:append_text(", websocket_len = " .. buf_len)

        local offset = 0
        local tag = buffer(offset, 1)

        subtree:add(f_fin, tag)

        local bo = GetBits(tag:uint(), 0, 0)

        if (bo ~= 1 and bo ~= 0) then
            subtree:append_text(", error:unknown byteorder:" .. bo)
            return false
        end

        subtree:add(f_reserved, tag)
        subtree:add(f_opcode, tag)

        local tag2 = buffer(offset, 2)
        local opcode = 0
        opcode = GetBits(tag2:uint(), 1, 7)
        -- pkt.cols.info = string.format("%-22s", theopcode[opcode])

        offset = offset + 1
        tag = buffer(offset, 1)

        local payloadlen = 0
        local mask = 0
        if buf_len > 2 then
            tag2 = buffer(offset, 2)
            payloadlen = GetBits(tag2:uint(), 1, 7)
            mask = GetBits(tag2:uint(), 0, 0)
        end

        pkt.cols.protocol = "SOON"
        -- if (opcode > 15) then
            -- pkt.cols.info = "-->it's not a websocket package<--"
        -- elseif (mask == 1 or mask == 0) then
        --     pkt.cols.info = string.format("%-22s", theopcode[opcode] .. themask[mask])
        -- else
        --     pkt.cols.info = string.format("%-22s", theopcode[opcode])
        --     -- return false
        -- end

        subtree:add(f_mask, tag)
        subtree:add(f_payloadlen, tag)

        offset = offset + 1

        local realpayloadlen = 0
        if (payloadlen == 126) then
            tag = buffer(offset, 2)
            subtree:add(f_extpayloadlen, tag)
            realpayloadlen = buffer(offset, 2):uint()
            offset = offset + 2
        else
            realpayloadlen = payloadlen
        end
        -- subtree:append_text(", realpayloadlen:" .. realpayloadlen)

        local maskstr
        local masktable = {}
        if (mask == 1) then
            tag = buffer(offset, 4)
            subtree:add(f_maskingkey, tag)
            local maskstr = buffer(offset, 4):bytes()
            for i = 1, 4 do
                masktable[i] = maskstr:get_index(i - 1)
            end
            -- subtree:append_text(", masktable:"..masktable[1]..masktable[2]..masktable[3]..masktable[4])
            offset = offset + 4
        end

        local maskedtable = {}
        local decodepayloadstr = {}
        if (realpayloadlen > 0) then
            local payload = buffer(offset)
            if (mask == 1) then
                local maskedstr = buffer(offset):bytes()
                for i = 1, realpayloadlen do
                    maskedtable[i] = maskedstr:get_index(i - 1)
                end
                -- subtree:append_text(", maskedtable:"..maskedtable[1]..maskedtable[2]..maskedtable[3]..maskedtable[4])

                -- subtree:append_text(", payloadstr0:"..payloadstr0)
                -- subtree:append_text(", mask:"..maskstr)
                -- local payloadstr = tostring(payload)
                -- subtree:append_text(", payload:"..payloadstr)
                -- local payloadstr0 = table.concat(maskedtable);
                -- subtree:append_text(", payloadstr0:"..payloadstr0)

                decodepayloadstr = XORMask(maskedtable, masktable)
                local hehe = ""
                -- for i = 1,#decodepayloadstr do
                for i = 1, realpayloadlen do
                    hehe = hehe .. string.char(decodepayloadstr[i])
                end
                -- subtree:append_text(", decodepayloadstr:"..hehe)
                -- subtree:append_text(", decodepayloadstr:"..table.concat(hehe))



                -- added by yby, 解析payload
                local cont = split(hehe, "\n")
                subtree:append_text(", soon_msg_len: "..payload:string():len())
                if (cont[1] == nil) then
                  cont[1] = ""
                end
                subtree:add(soon_msgId, cont[1])
                if (cont[2] == nil) then
                  cont[2] = ""
                end


                subtree:add(soon_msgType, cont[2])
                if (cont[3] == nil) then
                  cont[3] = ""
                end
                subtree:add(soon_msgContent, cont[3])
                -- subtree:add(f_payload, payload)
                if (cont[2] == "/config/train") then
                  pkt.cols.info = "CONFIGURING [train]"
                elseif (cont[2] == "/config/test") then
                  pkt.cols.info = "CONFIGURING [test]"
                elseif (cont[2] == "/config/model/type") then
                  pkt.cols.info = "CONFIGURING [model type]"
                elseif (cont[2] == "/config/model") then
                  pkt.cols.info = "CONFIGURING [model param]"
                elseif (string.find(cont[2], "/config/train/") ~= nil) then
                  pkt.cols.info = "CONFIGURING [train dataset]"
                elseif (cont[2] == "/control/start") then
                  pkt.cols.info = "CONTROL [start]"
                elseif (cont[2] == "/control/stop") then
                  pkt.cols.info = "CONTROL [stop]" 
                elseif (cont[2] == "/apply") then
                  pkt.cols.info = "APPLY"
                elseif (cont[2] == "/notify/apply") then
                  pkt.cols.info = "NOTIFY [apply]"
                elseif (cont[2] == "/eval") then
                  pkt.cols.info = "EVAL" 
                elseif (cont[2] == "/notify/eval") then
                  pkt.cols.info = "NOTIFY [eval]"
                elseif (cont[2] == "/control/delete/model") then
                  pkt.cols.info = "CONTROL [model deletion]"
                elseif (cont[2] == "/get/uri") then
                  pkt.cols.info = "GET [tensorboard]"
                elseif (cont[2] == "/notify/uri") then
                  pkt.cols.info = "NOTIFY [tensorboard]" 
                elseif (cont[2] == "/notify/train_end") then
                  pkt.cols.info = "NOTIFY [training end]"
                elseif (cont[2] == "/notify/train_dataset_end") then
                  pkt.cols.info = "NOTIFY [train dataset transmission end]"
                elseif (cont[2] == "/notify/process") then
                  pkt.cols.info = "PROCESS [update]"
                elseif (cont[2] == "/notify/test_dataset_end") then
                  pkt.cols.info = "NOTIFY [test dataset transmission end]"
                end
                -- subtree:add(f_payload, hehe)
            else

                local cont = split(payload:string(), "\n")
                subtree:append_text(", soon_msg_len: "..payload:string():len())
                if (cont[1] == nil) then
                  cont[1] = ""
                end
                subtree:add(soon_msgId, cont[1])
                if (cont[2] == nil) then
                  cont[2] = ""
                end


                subtree:add(soon_msgType, cont[2])
                if (cont[3] == nil) then
                  cont[3] = ""
                end
                subtree:add(soon_msgContent, cont[3])
                -- subtree:add(f_payload, payload)
                if (cont[2] == "/config/train") then
                  pkt.cols.info = "CONFIGURING [train]"
                elseif (cont[2] == "/config/test") then
                  pkt.cols.info = "CONFIGURING [test]"
                elseif (cont[2] == "/config/model/type") then
                  pkt.cols.info = "CONFIGURING [model type]"
                elseif (cont[2] == "/config/model") then
                  pkt.cols.info = "CONFIGURING [model param]"
                elseif (string.find(cont[2], "/config/train/") ~= nil) then
                  pkt.cols.info = "CONFIGURING [train dataset]"
                elseif (cont[2] == "/control/start") then
                  pkt.cols.info = "CONTROL [start]"
                elseif (cont[2] == "/control/stop") then
                  pkt.cols.info = "CONTROL [stop]" 
                elseif (cont[2] == "/apply") then
                  pkt.cols.info = "APPLY"
                elseif (cont[2] == "/notify/apply") then
                  pkt.cols.info = "NOTIFY [apply]"
                elseif (cont[2] == "/eval") then
                  pkt.cols.info = "EVAL" 
                elseif (cont[2] == "/notify/eval") then
                  pkt.cols.info = "NOTIFY [eval]"
                elseif (cont[2] == "/control/delete/model") then
                  pkt.cols.info = "CONTROL [model deletion]"
                elseif (cont[2] == "/get/uri") then
                  pkt.cols.info = "GET [tensorboard]"
                elseif (cont[2] == "/notify/uri") then
                  pkt.cols.info = "NOTIFY [tensorboard]" 
                elseif (cont[2] == "/notify/train_end") then
                  pkt.cols.info = "NOTIFY [training end]"
                elseif (cont[2] == "/notify/train_dataset_end") then
                  pkt.cols.info = "NOTIFY [train dataset transmission end]"
                elseif (cont[2] == "/notify/process") then
                  pkt.cols.info = "PROCESS [update]"
                elseif (cont[2] == "/notify/test_dataset_end") then
                  pkt.cols.info = "NOTIFY [test dataset transmission end]"
                end          
            end
        end

        return true
    end


    function p_websocket.dissector(buf, pkt, root)
        if p_websocket_dissector(buf, pkt, root) then
        else
            -- if not my procotol, call data
            -- get the packet's data field
            local data_dis = Dissector.get("data")
            data_dis:call(buf, pkt, root)
        end
    end


    -- register to tcp.port = 8888
    local websocket_disc_table = DissectorTable.get("tcp.port")
    websocket_disc_table:add(9999, p_websocket)
end
