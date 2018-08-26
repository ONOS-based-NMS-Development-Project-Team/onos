source ~/.bash_profile

编译onos的命令：onos-buck build onos --show-output
启动onos的命令：onos-buck run onos-local -- clean
查看onos控制台的命令：$ONOS_ROOT/tools/test/bin/onos localhost
安装app的命令：$ONOS_ROOT/tools/package/runtime/bin/onos-app localhost install target/
杀掉onos的命令：onos-kill localhost



通过buck增加第三方依赖
1. 在/lib/deps.json中增加相关依赖
2. 运行onos-lib-gen
