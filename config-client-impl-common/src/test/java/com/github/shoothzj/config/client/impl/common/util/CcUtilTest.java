package com.github.shoothzj.config.client.impl.common.util;

import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.module.FieldType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CcUtilTest {

    @Test
    public void getConfigNameNormal() {
        Assertions.assertEquals("TestConfig", CcUtil.getConfigName(TestConfig.class));
    }

    @Test
    public void getConfigFieldDescribeNormal() {
        final List<FieldDescribe> fieldDescribeList = CcUtil.getConfigFieldDescribe(TestConfig.class);
        Assertions.assertEquals(2, fieldDescribeList.size());
        {
            final FieldDescribe fieldDescribe = fieldDescribeList.get(0);
            Assertions.assertEquals("name", fieldDescribe.getName());
            Assertions.assertEquals(FieldType.STRING, fieldDescribe.getFieldType());
            Assertions.assertFalse(fieldDescribe.isAnonymous());
            Assertions.assertFalse(fieldDescribe.isSecret());
            Assertions.assertFalse(fieldDescribe.isRequired());
        }
        {
            final FieldDescribe fieldDescribe = fieldDescribeList.get(1);
            Assertions.assertEquals("age", fieldDescribe.getName());
            Assertions.assertEquals(FieldType.INT, fieldDescribe.getFieldType());
            Assertions.assertFalse(fieldDescribe.isAnonymous());
            Assertions.assertFalse(fieldDescribe.isSecret());
            Assertions.assertFalse(fieldDescribe.isRequired());
        }
    }
}
