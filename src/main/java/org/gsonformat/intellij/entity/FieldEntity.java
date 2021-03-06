package org.gsonformat.intellij.entity;


import org.apache.http.util.TextUtils;
import org.gsonformat.intellij.common.CheckUtil;
import org.jdesktop.swingx.ux.CellProvider;
import org.jdesktop.swingx.ux.Selector;
import org.json.JSONObject;

/**
 * Created by dim on 2015/7/15.
 */
public class FieldEntity implements Selector, CellProvider {

    protected String key;
    protected String type; //类型
    protected String fieldName; // 生成的名字
    protected String value; // 值
    protected ClassEntity targetClass; //依赖的实体类
    protected boolean generate = true;

    public ClassEntity getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(ClassEntity targetClass) {
        this.targetClass = targetClass;
    }

    public boolean isGenerate() {
        return generate;
    }

    public void setGenerate(boolean generate) {
        this.generate = generate;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getGenerateFieldName() {
        return CheckUtil.getInstant().handleArg(fieldName);
    }

    public void setFieldName(String fieldName) {
        if (TextUtils.isEmpty(fieldName)) {
            return;
        }
        this.fieldName = fieldName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public String getRealType() {
        if (targetClass != null) {
            return targetClass.getClassName();
        }
        if(nameContainsId() && isTypeInt()){
            type = "long";
        }
        return type;
    }

    public String getBriefType() {
        if (targetClass != null) {
            return targetClass.getClassName();
        }
        int i = type.indexOf(".");
        if (i > 0) {
            return type.substring(i);
        }
        if(nameContainsId() && isTypeInt()){
            type = "long";
        }
        return type;
    }

    private boolean nameContainsId(){
        if(TextUtils.isEmpty(fieldName)){
            return false;
        }
        String filedName = getGenerateFieldName();
        return filedName.contains("id") || filedName.contains("Id");
    }

    private boolean isTypeInt(){
        return "int".equals(type) || "Integer".equals(type);
    }

    public String getFullNameType() {

        String filedName = getGenerateFieldName();

        String filedType = type;
        if (targetClass != null) {
            filedType =  targetClass.getQualifiedName();
        }

        //如果key里带id/Id,且将被指定为int,Integer,那么自动将其指定为long
        if("int".equals(filedType) || "Integer".equals(filedType)){
            if(filedName.contains("id") || filedName.contains("Id")){
                filedType = "long";
            }
        }
        type = "long";

        return filedType;
    }

    public void setType(String type) {
        this.type = type;
        if(nameContainsId() && isTypeInt()){
            this.type = "long";
        }

    }

    public void checkAndSetType(String text) {
        if (type != null && CheckUtil.getInstant().checkSimpleType(type.trim())) {
            //基本类型
            if (CheckUtil.getInstant().checkSimpleType(text.trim())) {
                this.type = text.trim();
            }
        } else {
            //实体类:
            if (targetClass != null && !targetClass.isLock()) {
                if (!TextUtils.isEmpty(text)) {
                    targetClass.setClassName(text);
                }
            }
        }
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void setSelect(boolean select) {
        setGenerate(select);
    }

    public boolean isSameType(Object o) {
        if (o instanceof JSONObject) {
            if (targetClass != null) {
                return targetClass.isSame((JSONObject) o);
            }
        } else {
            return DataType.isSameDataType(DataType.typeOfString(type), DataType.typeOfObject(o));
        }
        return false;
    }

    @Override
    public String getCellTitle(int index) {
        String result = "";
        switch (index) {
            case 0:
                result = getKey();
                break;
            case 1:
                result = getValue();
                break;
            case 2:
                result = getBriefType();
                break;
            case 3:
                result = getFieldName();
                break;
        }
        return result;
    }

    @Override
    public void setValueAt(int column, String text) {
        switch (column) {
            case 2:
                checkAndSetType(text);
                break;
            case 3:
                if(CheckUtil.getInstant().containsDeclareFieldName(text)){
                    return;
                }
                CheckUtil.getInstant().removeDeclareFieldName(getFieldName());
                setFieldName(text);
                break;
        }
    }
}
