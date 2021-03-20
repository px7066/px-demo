package com.github.px.common.util;

import com.github.px.common.annotation.*;
import com.github.px.common.util.demo.DemoSource;
import com.github.px.common.util.demo.DemoSourceItem;
import com.github.px.common.util.demo.DemoTarget;
import com.github.px.common.util.demo.DemoTargetItem;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.ClassUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>复制工具类（属性名必须相同，或者定义别名）</p>
 * 扩展功能---@Alias 增加别名功能
 * 深度复制 (不支持Map，仅支持Collection的所有实现类)
 * 日期转换 默认为yyyy-MM-dd HH:mm:ss，可以通过DateFormat注解表明转换表达式
 * 枚举转换 默认调用枚举name(),可以通过--@EnumType 手动指定属性名
 * 新增数组复制方法 2020-07-20
 * 新增使用@EntityAlias子类的@EnumAlias和@DateFormat仍然会生效
 * 新增代理类 2020-07-21
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/3/4 14:44
 * @since 1.0
 */
public class CopyUtil {
    public static final ThreadLocal<SimpleDateFormat> SDF_LOCAL = new ThreadLocal<>();

    /**
     * 反射调用CachedIntrospectionResults的forClass和getPropertyDescriptor方法
     * @param clazz
     * @return
     * @throws BeansException
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeansException {
        Class<CachedIntrospectionResults> cachedIntrospectionResultsClass = CachedIntrospectionResults.class;
        try {
            Method forClass = cachedIntrospectionResultsClass.getDeclaredMethod("forClass", Class.class);
            forClass.setAccessible(true);
            CachedIntrospectionResults cr = (CachedIntrospectionResults) forClass.invoke(null , clazz);
            Method getPropertyDescriptors = cachedIntrospectionResultsClass.getDeclaredMethod("getPropertyDescriptors");
            getPropertyDescriptors.setAccessible(true);
            return (PropertyDescriptor[]) getPropertyDescriptors.invoke(cr);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射获取某个属性的方法
     * @param clazz
     * @param propertyName
     * @return
     * @throws BeansException
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) throws BeansException {
        Class<CachedIntrospectionResults> cachedIntrospectionResultsClass = CachedIntrospectionResults.class;
        try {
            Method forClass = cachedIntrospectionResultsClass.getDeclaredMethod("forClass", Class.class);
            forClass.setAccessible(true);
            CachedIntrospectionResults cr = (CachedIntrospectionResults) forClass.invoke(null , clazz);
            Method getPropertyDescriptor = cachedIntrospectionResultsClass.getDeclaredMethod("getPropertyDescriptor", String.class);
            getPropertyDescriptor.setAccessible(true);
            return (PropertyDescriptor) getPropertyDescriptor.invoke(cr, propertyName);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T>T copyProperties(Object source, Class<T> tClass){
        return copyProperties(source, tClass, false, null, null);
    }

    /**
     * 复制属性，目标属性为空对象
     * @param source
     * @param tClass
     * @param <T>
     * @return
     */
    public static  <T>T copyProperties(Object source, Class<T> tClass,
                                       boolean proxy, Callback[] callbacks, CallbackFilter callbackFilter) {
        if(source == null){
            return null;
        }
        T target;
        try {
            if(proxy){
                target = ProxyUtil.getInstance(tClass, callbacks, callbackFilter);
            }else{
                Constructor<T> constructor = tClass.getConstructor();
                constructor.setAccessible(true);
                target = tClass.newInstance();
            }
            PropertyDescriptor[] targetPds = getPropertyDescriptors(tClass);
            PropertyDescriptor[] var7 = targetPds;
            int var8 = targetPds.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                PropertyDescriptor targetPd = var7[var9];
                Method writeMethod = targetPd.getWriteMethod();

                if (writeMethod != null) {
                    Parameter[] parameters = writeMethod.getParameters();
                    Parameter parameter = parameters[0];
                    String fieldName = parameter.getName();
                    Field field = null;
                    Class<?> zClass = tClass;
                    while (zClass != null){
                        try {
                            field = zClass.getDeclaredField(fieldName);
                            break;
                        }catch (NoSuchFieldException e){
                            zClass = zClass.getSuperclass();
                        }
                    }
                    if(field == null){
                        continue;
                    }
                    String targetPdName = targetPd.getName();
                    if(field.isAnnotationPresent(Alias.class)){
                        Alias alias = field.getAnnotation(Alias.class);
                        targetPdName = alias.value();
                    }
                    Ignore ignore = null;
                    if(field.isAnnotationPresent(Ignore.class)){
                        ignore = field.getAnnotation(Ignore.class);
                    }
                    if(ignore != null){
                        continue;
                    }
                    EntityAlias entityAlias = null;
                    if(field.isAnnotationPresent(EntityAlias.class)){
                        entityAlias = field.getAnnotation(EntityAlias.class);
                    }
                    PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPdName);
                    if (sourcePd != null) {
                        Method readMethod = sourcePd.getReadMethod();
                        if (readMethod != null) {
                            try {
                                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                    readMethod.setAccessible(true);
                                }
                                Object value = readMethod.invoke(source);
                                if(value == null){
                                    continue;
                                }
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                if(isCollection(readMethod.getReturnType()) && isCollection(parameter.getType())){
                                    writeArray(target, writeMethod, field, (Collection<?>) value);
                                    //单一属性复制
                                } else if(readMethod.getReturnType().isAssignableFrom(Date.class) && parameter.getType().isAssignableFrom(String.class)) {
                                    writeDate(target, writeMethod, field, (Date) value);
                                } else if(readMethod.getReturnType().isEnum() && parameter.getType().isAssignableFrom(String.class)){
                                    writeEnum(target, writeMethod, field, value);
                                } else if(Number.class.isAssignableFrom(readMethod.getReturnType())) {
                                    writNumber(target, writeMethod, field, value);
                                } else if(entityAlias != null){
                                    writeEntityField(target, writeMethod, field, value);
                                } else if(ClassUtils.isAssignable(parameter.getType(), readMethod.getReturnType())){
                                    writeMethod.invoke(target, value);
                                } else if(!ClassUtils.isAssignable(parameter.getType(), readMethod.getReturnType())){
                                    Object val = copyProperties(value, parameter.getType());
                                    writeMethod.invoke(target, val);
                                }

                            } catch (Throwable var15) {
                                throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", var15);
                            }
                        }
                    }else {
                        if(entityAlias != null){
                            writeEntityField(target, writeMethod, field, source);
                        }
                    }
                }
            }
            return target;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> void writeEntityField(T target, Method writeMethod, Field field, Object value) {
        EntityAlias entityAlias = null;
        if(field.isAnnotationPresent(EntityAlias.class)){
            entityAlias = field.getAnnotation(EntityAlias.class);
        }
        Class vClass = value.getClass();
        assert entityAlias != null;
        String fieldName = entityAlias.value();
        String[] child = entityAlias.child();
        Object val = null;
        try{
            if(child.length != 0){
                for (String s : child) {
                    fieldName = s;
                    PropertyDescriptor pd = new PropertyDescriptor(fieldName, vClass);
                    Method readMethod = pd.getReadMethod();
                    if (readMethod != null) {
                        val = readMethod.invoke(value);
                        if (val == null) {
                            break;
                        } else {
                            value = val;
                            vClass = val.getClass();
                        }
                    }
                }
            }else {
                PropertyDescriptor pd = new PropertyDescriptor(fieldName, vClass);
                Method readMethod = pd.getReadMethod();
                if(readMethod != null){
                    val = readMethod.invoke(value);
                }
            }
            if(val != null){
                if(val.getClass().isEnum() && writeMethod.getParameterTypes()[0].isAssignableFrom(String.class)){
                    writeEnum(target, writeMethod, field, val);
                }else if(val.getClass().isAssignableFrom(Date.class) && writeMethod.getParameterTypes()[0].isAssignableFrom(String.class)) {
                    writeDate(target, writeMethod, field, (Date) value);
                }else {
                    writeMethod.invoke(target, val);
                }
            }
        }catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private static <T> void writeDate(T target, Method writeMethod, Field field, Date value) throws IllegalAccessException, InvocationTargetException {
        Date dataVal = value;

        if(SDF_LOCAL.get() == null){
            SDF_LOCAL.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }
        SimpleDateFormat sdf;
        DateFormat dateFormat = null;
        if(field.isAnnotationPresent(DateFormat.class)){
            dateFormat = field.getAnnotation(DateFormat.class);
        }
        if(dateFormat == null){
            sdf = SDF_LOCAL.get();
        }else {
            sdf = new SimpleDateFormat(dateFormat.value());
        }
        String stringVal = sdf.format(dataVal);
        writeMethod.invoke(target, stringVal);
    }

    private static <T> void writeEnum(T target, Method writeMethod, Field field, Object value) throws IllegalAccessException, InvocationTargetException {
        EnumAlias enumAlias = null;
        if(field.isAnnotationPresent(EnumAlias.class)){
            enumAlias = field.getAnnotation(EnumAlias.class);
        }
        if(enumAlias != null && !enumAlias.name()){
            PropertyDescriptor propertyDescriptor = getPropertyDescriptor(value.getClass(), enumAlias.value());
            if(propertyDescriptor != null){
                String enumVal = (String) propertyDescriptor.getReadMethod().invoke(value);
                writeMethod.invoke(target, enumVal);
            }
        }else{
            Enum anEnum = (Enum) value;
            writeMethod.invoke(target, anEnum.name());
        }
    }

    private static <T> void writNumber(T target, Method writeMethod, Field field, Object value) throws IllegalAccessException, InvocationTargetException {
        NumberFormat numberFormat = null;
        if(field.isAnnotationPresent(NumberFormat.class)){
            numberFormat= field.getAnnotation(NumberFormat.class);
        }
        //数值处理
        if(numberFormat != null){
            DecimalFormat decimalFormat = new DecimalFormat(numberFormat.pattern());
            if(Integer.class.isAssignableFrom(value.getClass())){
                writeMethod.invoke(target, Integer.valueOf(decimalFormat.format(value)));
            }else if(Double.class.isAssignableFrom(value.getClass())){
                writeMethod.invoke(target, Double.valueOf(decimalFormat.format(value)));
            }else if(BigDecimal.class.isAssignableFrom(value.getClass())){
                writeMethod.invoke(target, new BigDecimal(decimalFormat.format(value)));
            }else {
                writeMethod.invoke(target, value);
            }
        }else {
            writeMethod.invoke(target, value);
        }
    }

    /**
     * 复制属性
     * @param source 源对象
     * @param target 目的对象
     * @param overload 是否覆盖已存在的属性
     */
    public static void copyProperties(Object source, Object target, boolean overload){
        copyProperties(source, target, overload, null, true);
    }

    /**
     * 复制属性（覆盖已有属性，深度克隆）
     * @param source 源对象
     * @param target 目的对象
     */
    public static void copyProperties(Object source, Object target){
        copyProperties(source,target, true, null, true);
    }

    /**
     * 复制属性(覆盖已有属性， 深度克隆)
     * @param source 源对象
     * @param target 目的对象
     * @param ignoreProperties 忽略属性值
     */
    public static void copyProperties(Object source, Object target, String ignoreProperties){
         copyProperties(source,target, true, ignoreProperties, true);
    }

    /**
     * 复制属性
     * @param source 源独享
     * @param target 目的对象
     * @param overload 是否覆盖
     * @param ignoreProperties 忽略属性值
     * @param deepClone 深度克隆
     */
    public static void copyProperties(Object source, Object target,
                                      boolean overload, String ignoreProperties, boolean deepClone) {
        if(target == null){
            return;
        }
        Class<?> actualEditable = target.getClass();

        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Collections.singletonList(ignoreProperties) : null);
        if(targetPds == null){
            return;
        }

        for (PropertyDescriptor targetPd : targetPds) {
            //不覆盖
            if(!overload){
                Method readMethod = targetPd.getReadMethod();
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                try {
                    Object value = readMethod.invoke(target);
                    if(value != null){
                        continue;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new FatalBeanException(
                            "Could not copy property '" + targetPd.getName() + "' from source to target", e);
                }
            }
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                Parameter[] parameters = writeMethod.getParameters();
                Parameter parameter = parameters[0];
                String fieldName = parameter.getName();
                Field field = null;
                Class<?> zClass = target.getClass();
                while (zClass != null){
                    try {
                        field = zClass.getDeclaredField(fieldName);
                        break;
                    }catch (NoSuchFieldException e){
                        zClass = zClass.getSuperclass();
                    }
                }
                if(field == null){
                    continue;
                }
                String targetPdName = targetPd.getName();
                if(field.isAnnotationPresent(Alias.class)){
                    Alias alias = field.getAnnotation(Alias.class);
                    targetPdName = alias.value();
                }
                Ignore ignore = null;
                if(field.isAnnotationPresent(Ignore.class)){
                    ignore = field.getAnnotation(Ignore.class);
                }
                if(ignore != null){
                    continue;
                }
                EntityAlias entityAlias = null;
                if(field.isAnnotationPresent(EntityAlias.class)){
                    entityAlias = field.getAnnotation(EntityAlias.class);
                }
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPdName);
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if(readMethod != null){
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            if(value == null){
                                writeMethod.invoke(target, value);
                                continue;
                            }
                            //数组赋值
                            if(deepClone && isCollection(readMethod.getReturnType()) && isCollection(parameter.getType())){
                                writeArray(target, writeMethod, field, (Collection<?>) value);
                                //单一属性复制
                            }  else if(readMethod.getReturnType().isAssignableFrom(Date.class) && parameter.getType().isAssignableFrom(String.class)) {
                                writeDate(target, writeMethod, field, (Date) value);
                            } else if(readMethod.getReturnType().isEnum() && parameter.getType().isAssignableFrom(String.class)){
                                writeEnum(target, writeMethod, field, value);
                            } else if(Number.class.isAssignableFrom(readMethod.getReturnType())) {
                                writNumber(target, writeMethod, field, value);
                            }  else if(entityAlias != null){
                                writeEntityField(target, writeMethod, field, value);
                            } else if (ClassUtils.isAssignable(parameter.getType(), readMethod.getReturnType())) {
                                writeMethod.invoke(target, value);
                            } else if(deepClone && !ClassUtils.isAssignable(parameter.getType(), readMethod.getReturnType())){
                                Object val = copyProperties(value, parameter.getType());
                                writeMethod.invoke(target, val);
                            }


                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    private static void writeArray(Object target, Method writeMethod, Field field, Collection<?> value) throws IllegalAccessException, InvocationTargetException {
        Collection<?> readValues = value;
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Collection<Object> args;
            if("java.util.Set".equals(pt.getRawType().getTypeName())){
                args = new HashSet<>();
            }else {
                args = new ArrayList<>();
            }
            Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
            for (Object readValue : readValues) {
                //如果类型相同直接写入（不深度复制）
                if(readValue.getClass().equals(actualTypeArgument)){
                    args.addAll(readValues);
                    writeMethod.invoke(target, args);
                    return;
                }
                if(readValue.getClass().isEnum() && actualTypeArgument.isAssignableFrom(String.class)){
                    EnumAlias enumAlias = null;
                    if(field.isAnnotationPresent(EnumAlias.class)){
                        enumAlias = field.getAnnotation(EnumAlias.class);
                    }
                    if(enumAlias != null && !enumAlias.name()){
                        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(readValue.getClass(), enumAlias.value());
                        if(propertyDescriptor != null){
                            String enumVal = (String) propertyDescriptor.getReadMethod().invoke(readValue);
                            args.add(enumVal);
                        }
                    }else{
                        Enum anEnum = (Enum) readValue;
                        args.add(anEnum.name());
                    }
                    // 如果是Object -> String, 默认取对象的id
                }else if(String.class.isAssignableFrom(actualTypeArgument)){
                    args.add(ReflectionUtils.getField("id", readValue));
                }else {
                    Object arg = CopyUtil.copyProperties(readValue, actualTypeArgument);
                    args.add(arg);
                }


            }
            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
            }
            writeMethod.invoke(target, args);
        }
    }

    private static boolean isCollection(Class<?> clazz){
        return Collection.class.isAssignableFrom(clazz);
    }

    public static <T>List<T> copyArray(List source, Class<T> targetClass){
        List<T> target = new ArrayList<>();
        if(source == null || source.isEmpty()){
            return target;
        }
        Class sourceClass = source.get(0).getClass();
        if(sourceClass != null){
            for (Object o : source) {
                T t = copyProperties(o, targetClass);
                target.add(t);
            }
        }
        return target;
    }

    public static <T>Set<T> copySet(Set source, Class<T> targetClass){
        Set<T> target = new HashSet<>();
        if(source == null || source.isEmpty()){
            return target;
        }
        Class sourceClass = source.iterator().next().getClass();
        if(sourceClass != null){
            for (Object o : source) {
                T t = copyProperties(o, targetClass);
                target.add(t);
            }
        }
        return target;
    }

    public static void main(String[] args) {
        DemoSource demoSource = new DemoSource();
        demoSource.setName1("张三");
        demoSource.setNow(new Date());
        demoSource.setPassword("123456");
        demoSource.setDCount(1.236056d);
        demoSource.setDemoSourceItem(new DemoSourceItem("王五", 10, new Date()));
        DemoTarget target = new DemoTarget();
        CopyUtil.copyProperties(demoSource, target,false);
        System.out.println(target.toString());
        DemoTarget b = CopyUtil.copyProperties(demoSource, DemoTarget.class);
        System.out.println(b);

        //深度复制测试
        DemoSource demoSource1 = new DemoSource();
        demoSource1.setName1("李四");
        demoSource1.setPassword("123456");
        demoSource1.setNow(new Date());
        List<DemoSourceItem> items = new ArrayList<>();
        items.add(new DemoSourceItem("王五", 10, new Date()));
        items.add(new DemoSourceItem("赵六", 20, new Date()));
        demoSource1.setSourceItems(new HashSet<>(items));

        DemoTarget target1 = new DemoTarget();
        CopyUtil.copyProperties(demoSource1, target1);
        System.out.println(target1);


        List<DemoSourceItem> items2 =  new ArrayList<>();
        items2.add(new DemoSourceItem("王五", 10, new Date()));
        items2.add(new DemoSourceItem("赵六", 20, new Date()));
        List<DemoTargetItem> targetItems = CopyUtil.copyArray(items2, DemoTargetItem.class);
        System.out.println(targetItems);


    }

}
