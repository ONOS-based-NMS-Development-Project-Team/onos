source ~/.bash_profile

编译onos的命令：onos-buck build onos --show-output
启动onos的命令：onos-buck run onos-local -- clean
查看onos控制台的命令：$ONOS_ROOT/tools/test/bin/onos localhost
安装app的命令：$ONOS_ROOT/tools/package/runtime/bin/onos-app localhost install target/
杀掉onos的命令：onos-kill localhost

从csv文件中导入数据库的示例：copy failure_class (id, train, dataid, level0, name0, node0, board0, time0, level1, name1, node1, board1, time1, class) from '/database/data/StateGrid/resources/故障类型判别数据集/alarm_input/data3.csv' delimiter ',' csv;

通过buck增加第三方依赖
1. 在/lib/deps.json中增加相关依赖
2. 运行onos-lib-gen
