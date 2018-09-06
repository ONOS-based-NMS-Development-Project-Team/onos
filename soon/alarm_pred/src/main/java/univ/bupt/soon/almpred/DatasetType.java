package univ.bupt.soon.almpred;


/**
 * 数据集的类型.
 *
 */
public enum DatasetType {
    // 由输入光功率当前值预测IN_PWR_LOW告警
    IN_PWR_LOW() {
        @Override
        public String getName() {
            return "IN_PWR_LOW";
        }
    },
    //    由输入光功率当前值
    //    告警：OUT_PWR_ABN；性能：泵浦激光器背光检测电流当前值(uA)、泵浦激光器制冷电流当前值(mA)、泵浦激光器工作电流当前值(mA)、输出光功率当前值(dBm)
    //    告警：R_LOS；性能：输入光功率当前值
    COMBINATION() {
        @Override
        public String getName() {
            return "COMBINATION";
        }
    };

    public String getName() {
        return null;
    }
}
