package com.github.shoothzj.config.client.impl.common.util;

import com.github.shoothzj.config.client.impl.common.module.FieldDescribe;
import com.github.shoothzj.config.client.impl.common.module.FieldType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CcUtilTest {

    @Test
    public void getConfigNameNormal() {
        Assert.assertEquals("TestConfig", CcUtil.getConfigName(TestConfig.class));
    }

    @Test
    public void getConfigFieldDescribeNormal() {
        final List<FieldDescribe> fieldDescribeList = CcUtil.getConfigFieldDescribe(TestConfig.class);
        Assert.assertEquals(2, fieldDescribeList.size());
        {
            final FieldDescribe fieldDescribe = fieldDescribeList.get(0);
            Assert.assertEquals("name", fieldDescribe.getName());
            Assert.assertEquals(FieldType.STRING, fieldDescribe.getFieldType());
            Assert.assertFalse(fieldDescribe.isAnonymous());
            Assert.assertFalse(fieldDescribe.isSecret());
            Assert.assertFalse(fieldDescribe.isRequired());
        }
        {
            final FieldDescribe fieldDescribe = fieldDescribeList.get(1);
            Assert.assertEquals("age", fieldDescribe.getName());
            Assert.assertEquals(FieldType.INT, fieldDescribe.getFieldType());
            Assert.assertFalse(fieldDescribe.isAnonymous());
            Assert.assertFalse(fieldDescribe.isSecret());
            Assert.assertFalse(fieldDescribe.isRequired());
        }
    }
}