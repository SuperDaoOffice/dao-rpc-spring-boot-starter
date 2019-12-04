package com.dao.rpc.server.util;

import org.springframework.util.StringUtils;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @author HuChiHui
 * @date 2019/04/12 上午 10:47
 * @description
 */
public class Validator {

    private boolean valid = true;
    private String message;

    public static Validator create() {
        return new Validator();
    }

    private Validator() {
    }

    public boolean isValid() {
        return this.valid;
    }

    public String getMessage() {
        return this.message;
    }

    public Validator notNull(Object fieldValue, String message) {
        if (this.valid && null == fieldValue) {
            this.valid = false;
            this.message = message + "不能为空";
        }
        return this;
    }

    public Validator notBlank(String fieldValue, String message) {
        if (this.valid && !StringUtils.hasText(fieldValue)) {
            this.valid = false;
            this.message = message + "不能为空";
        }
        return this;
    }

    public Validator notEmpty(Collection<?> collection, String message) {
        if (this.valid && (null == collection || collection.isEmpty())) {
            this.valid = false;
            this.message = message + "不能为空";
        }
        return this;
    }

    public Validator length(String fieldValue, int length, String message) {
        if (this.valid && !(fieldValue.trim().length() == length)) {
            this.valid = false;
            this.message = message + "长度不等于" + length;
        }
        return this;
    }

    public Validator dynamicLength(String fieldValue, int min, int max, String message) {
        if (this.valid && !(fieldValue.trim().length() >= min && fieldValue.trim().length() <= max)) {
            this.valid = false;
            this.message = message + "长度不在 " + min + "-" + max + " 之间";
        }
        return this;
    }

    public Validator patten(Object fieldValue, String regex, String message) {
        Pattern pattern = Pattern.compile(regex);
        if (this.valid && !pattern.matcher(String.valueOf(fieldValue)).matches()) {
            this.valid = false;
            this.message = message + "不符合格式要求";
        }
        return this;
    }

    public Validator ifNotBlankLength(String fieldValue, int length, String message) {
        if (!StringUtils.isEmpty(fieldValue)) {
            if (this.valid && fieldValue.length() != length) {
                this.valid = false;
                this.message = message + "长度不等于" + length;
            }
        }
        return this;
    }

    public Validator ifNotBlankLength(String fieldValue, int min, int max, String message) {
        if (!StringUtils.isEmpty(fieldValue)) {
            if (this.valid && !(fieldValue.length() >= min && fieldValue.length() <= max)) {
                this.valid = false;
                this.message = message + "长度不在 " + min + "-" + max + " 之间";
            }
        }
        return this;
    }

    public void validate() {
        if (!this.valid) {
            throw new InvalidParameterException(this.message);
        }
    }

}
