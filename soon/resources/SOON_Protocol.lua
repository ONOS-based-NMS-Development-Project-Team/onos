-- @brief SOON Protocol dissector plugin based on WebSocket
-- @author yby
-- @date 208.09.19

-- define split function
function split(inputstr, sep)
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

-- create a new dissector
SOONoverWebsocket = Proto("SOONoverWebsocket", "SOON over websocket")

-- add fields
local fields = SOONoverWebsocket.fields
fields.msgId = ProtoField.string(".msgId", "Message Identifier")
fields.type = ProtoField.string(".type", "Message Type")
fields.content = ProtoField.string(".content", "Message Content")

-- dissect packet
function SOONoverWebsocket.dissector(tvb, pinfo, tree)
  local subtree = tree:add(SOONoverWebsocket, tvb())
  pinfo.cols.protocol = SOONoverWebsocket.name

  local msg = tostring(tvb())
  local cont = split(msg, "\n")
  subtree:add(fields.msgId, ByteArray.tvb(ByteArray.new(cont[1])))
  subtree:add(fields.type, ByteArray.tvb(ByteArray.new(cont[2])))
  subtree:add(fields.content, ByteArray.tvb(ByteArray.new(cont[3])))
end

-- register this dissector
soon_dissector = DissectorTable.get("ws.port"):add(9999, SOONoverWebsocket)
