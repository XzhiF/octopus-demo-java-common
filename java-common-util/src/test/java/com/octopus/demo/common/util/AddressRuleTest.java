package com.octopus.demo.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AddressRuleTest {

    private final AddressRule rule = new AddressRule();

    @Test
    @DisplayName("type is ADDRESS")
    void type_isAddress() {
        assertThat(rule.type()).isEqualTo(MaskType.ADDRESS);
    }

    @Test
    @DisplayName("address after label keeps admin chain and stars details")
    void find_addressWithLabel_masksDetails() {
        assertThat(rule.find("地址：北京市朝阳区建国路88号"))
                .isEqualTo(List.of(new MaskHit(3, 15, "北京市朝阳区****")));
    }

    @Test
    @DisplayName("province + city + district chain is kept")
    void find_threeLevelChain_masksDetails() {
        assertThat(rule.find("浙江省杭州市西湖区文三路100号"))
                .isEqualTo(List.of(new MaskHit(0, 16, "浙江省杭州市西湖区****")));
    }

    @Test
    @DisplayName("detail covering a building is fully swallowed")
    void find_buildingDetail_fullyMasked() {
        assertThat(rule.find("收货地址：广东省深圳市南山区科技园大厦"))
                .isEqualTo(List.of(new MaskHit(5, 19, "广东省深圳市南山区****")));
    }

    @Test
    @DisplayName("district without province/city chain is not a hit")
    void find_districtOnly_noHit() {
        assertThat(rule.find("朝阳区建国路88号")).isEmpty();
    }

    @Test
    @DisplayName("admin chain without detail feature is not a hit")
    void find_adminOnly_noHit() {
        assertThat(rule.find("北京市朝阳区")).isEmpty();
    }

    @Test
    @DisplayName("null and empty return empty list")
    void find_nullAndEmpty_empty() {
        assertThat(rule.find(null)).isEmpty();
        assertThat(rule.find("")).isEmpty();
    }
}
