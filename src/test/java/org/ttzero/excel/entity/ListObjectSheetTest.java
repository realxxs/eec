/*
 * Copyright (c) 2017-2019, guanquan.wang@yandex.com All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ttzero.excel.entity;

import org.junit.Test;
import org.ttzero.excel.Print;
import org.ttzero.excel.annotation.ExcelColumn;
import org.ttzero.excel.annotation.IgnoreExport;
import org.ttzero.excel.entity.style.Fill;
import org.ttzero.excel.entity.style.PatternType;
import org.ttzero.excel.entity.style.Styles;
import org.ttzero.excel.manager.docProps.Core;
import org.ttzero.excel.processor.IntConversionProcessor;
import org.ttzero.excel.processor.StyleProcessor;
import org.ttzero.excel.reader.ExcelReader;
import org.ttzero.excel.reader.ExcelReaderTest;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.ttzero.excel.Print.println;
import static org.ttzero.excel.reader.ExcelReaderTest.testResourceRoot;

/**
 * @author guanquan.wang at 2019-04-28 19:17
 */
public class ListObjectSheetTest extends WorkbookTest {

    @Test
    public void testWrite() throws IOException {
        new Workbook("test object", author)
            .watch(Print::println)
            .addSheet(Item.randomTestData())
            .writeTo(defaultTestPath);
    }

    @Test public void testAllTypeWrite() throws IOException {
        new Workbook("all type object", author)
            .watch(Print::println)
            .addSheet(AllType.randomTestData())
            .writeTo(defaultTestPath);
    }

    @Test public void testAnnotation() throws IOException {
        new Workbook("annotation object", author)
            .watch(Print::println)
            .addSheet(Student.randomTestData())
            .writeTo(defaultTestPath);
    }

    @Test public void testAnnotationAutoSize() throws IOException {
        new Workbook("annotation object auto-size", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<>(Student.randomTestData()))
            .writeTo(defaultTestPath);
    }

    @Test public void testStringWaterMark() throws IOException {
        new Workbook("object string water mark", author)
            .watch(Print::println)
            .setWaterMark(WaterMark.of("SECRET"))
            .addSheet(Item.randomTestData())
            .writeTo(defaultTestPath);
    }

    @Test public void testLocalPicWaterMark() throws IOException {
        new Workbook("object local pic water mark", author)
            .watch(Print::println)
            .setWaterMark(WaterMark.of(testResourceRoot().resolve("mark.png")))
            .addSheet(Item.randomTestData())
            .writeTo(defaultTestPath);
    }

    @Test public void testStreamWaterMark() throws IOException {
        new Workbook("object input stream water mark", author)
            .watch(Print::println)
            .setWaterMark(WaterMark.of(getClass().getClassLoader().getResourceAsStream("mark.png")))
            .addSheet(Item.randomTestData())
            .writeTo(defaultTestPath);
    }

    @Test public void testAutoSize() throws IOException {
        new Workbook("all type auto size", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(AllType.randomTestData())
            .writeTo(defaultTestPath);
    }

    @Test public void testIntConversion() throws IOException {
        new Workbook("test int conversion", author)
            .watch(Print::println)
            .addSheet(Student.randomTestData()
                , new Sheet.Column("学号", "id")
                , new Sheet.Column("姓名", "name")
                , new Sheet.Column("成绩", "score", n -> n < 60 ? "不及格" : n)
            )
            .writeTo(defaultTestPath);
    }

    @Test public void testStyleConversion() throws IOException {
        new Workbook("object style processor", author)
            .watch(Print::println)
            .addSheet(Student.randomTestData()
                , new Sheet.Column("学号", "id")
                , new Sheet.Column("姓名", "name")
                , new Sheet.Column("成绩", "score")
                    .setStyleProcessor((o, style, sst) -> {
                        if ((int)o < 60) {
                            style = Styles.clearFill(style)
                                | sst.addFill(new Fill(PatternType.solid, Color.orange));
                        }
                        return style;
                    })
            )
            .writeTo(defaultTestPath);
    }

    @Test public void testConvertAndStyleConversion() throws IOException {
        new Workbook("object style and style processor", author)
            .watch(Print::println)
            .addSheet(Student.randomTestData()
                , new Sheet.Column("学号", "id")
                , new Sheet.Column("姓名", "name")
                , new Sheet.Column("成绩", "score", n -> n < 60 ? "不及格" : n)
                    .setStyleProcessor((o, style, sst) -> {
                        if ((int)o < 60) {
                            style = Styles.clearFill(style)
                                | sst.addFill(new Fill(PatternType.solid, new Color(246, 209, 139)));
                        }
                        return style;
                    })
            )
            .writeTo(defaultTestPath);
    }

    @Test public void testCustomizeDataSource() throws IOException {
        new Workbook("customize datasource", author)
            .watch(Print::println)
            .addSheet(new CustomizeDataSourceSheet())
            .writeTo(defaultTestPath);
    }

    @Test public void testBoxAllTypeWrite() throws IOException {
        new Workbook("box all type object", author)
            .watch(Print::println)
            .addSheet(BoxAllType.randomTestData())
            .writeTo(defaultTestPath);
    }

    // -----AUTO SIZE

    @Test public void testBoxAllTypeAutoSizeWrite() throws IOException {
        new Workbook("auto-size box all type object", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(BoxAllType.randomTestData())
            .writeTo(defaultTestPath);
    }

    @Test public void testCustomizeDataSourceAutoSize() throws IOException {
        new Workbook("auto-size customize datasource", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new CustomizeDataSourceSheet())
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor1() throws IOException {
        new Workbook("test list sheet Constructor1", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<Item>())
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor2() throws IOException {
        new Workbook("test list sheet Constructor2", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<Item>("Item").setData(Item.randomTestData(10)))
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor3() throws IOException {
        new Workbook("test list sheet Constructor3", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<Item>("Item"
                , new Sheet.Column("ID", "id")
                , new Sheet.Column("NAME", "name"))
                .setData(Item.randomTestData(10)))
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor4() throws IOException {
        new Workbook("test list sheet Constructor4", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<Item>("Item", WaterMark.of(author)
                , new Sheet.Column("ID", "id")
                , new Sheet.Column("NAME", "name"))
                .setData(Item.randomTestData(10)))
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor5() throws IOException {
        new Workbook("test list sheet Constructor5", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<>(Item.randomTestData(10)))
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor6() throws IOException {
        new Workbook("test list sheet Constructor6", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<>("ITEM", Item.randomTestData(10)))
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor7() throws IOException {
        new Workbook("test list sheet Constructor7", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<>(Item.randomTestData(10)
                , new Sheet.Column("ID", "id")
                , new Sheet.Column("NAME", "name")))
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor8() throws IOException {
        new Workbook("test list sheet Constructor8", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<>("ITEM", Item.randomTestData(10)
                , new Sheet.Column("ID", "id")
                , new Sheet.Column("NAME", "name")))
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor9() throws IOException {
        new Workbook("test list sheet Constructor9", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<>(Item.randomTestData(10)
                , WaterMark.of(author)
                , new Sheet.Column("ID", "id")
                , new Sheet.Column("NAME", "name")))
            .writeTo(defaultTestPath);
    }

    @Test public void testConstructor10() throws IOException {
        new Workbook("test list sheet Constructor10", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<>("ITEM"
                , Item.randomTestData(10)
                , WaterMark.of(author)
                , new Sheet.Column("ID", "id")
                , new Sheet.Column("NAME", "name")))
            .writeTo(defaultTestPath);
    }

    @Test public void testArray() throws IOException {
        new Workbook()
            .watch(Print::println)
            .addSheet(new ListSheet<>()
                .setData(Arrays.asList(new Item(1, "abc"), new Item(2, "xyz"))))
            .writeTo(defaultTestPath);
    }

    @Test public void testSingleList() throws IOException {
        new Workbook()
            .watch(Print::println)
            .addSheet(new ListSheet<>()
                .setData(Collections.singletonList(new Item(1, "a b c"))))
            .writeTo(defaultTestPath);
    }

    public static StyleProcessor sp = (o, style, sst) -> {
        if ((int)o < 60) {
            style = Styles.clearFill(style)
                | sst.addFill(new Fill(PatternType.solid, Color.orange));
        }
        return style;
    };

    // 定义一个int值转换lambda表达式，成绩低于60分显示"不及格"，其余显示正常分数
    public static IntConversionProcessor conversion = n -> n < 60 ? "不及格" : n;

    @Test
    public void testStyleConversion1() throws IOException {
        new Workbook("object style processor1", "guanquan.wang")
            .addSheet(new ListSheet<>("期末成绩", Student.randomTestData()
                    , new Sheet.Column("学号", "id")
                    , new Sheet.Column("姓名", "name")
                    , new Sheet.Column("成绩", "score", conversion)
                    .setStyleProcessor(sp)
                )
            )
            .writeTo(defaultTestPath);
    }

    @Test public void testNullValue() throws IOException {
        new Workbook("test null value", author)
            .watch(Print::println)
            .setAutoSize(true)
            .addSheet(new ListSheet<>("EXT-ITEM", ExtItem.randomTestData(10)))
            .writeTo(defaultTestPath);
    }

    @Test public void testReflect() throws IntrospectionException, IllegalAccessException {
        PropertyDescriptor[] array = Introspector.getBeanInfo(ExtItem.class).getPropertyDescriptors();
        for (PropertyDescriptor pd : array) {
            println(pd);
        }
        ExtItem item = new ExtItem(1, "guanquan.wang");
        item.nice = "colvin";

        Field[] fields = item.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            println(field + ": " + field.get(item));
        }
    }

    @Test public void testFieldUnDeclare() throws IOException {
        try {
            new Workbook("field un-declare", author)
                .addSheet(new ListSheet<>("期末成绩", Student.randomTestData()
                        , new Sheet.Column("学号", "id")
                        , new Sheet.Column("姓名", "name")
                        , new Sheet.Column("成绩", "sore") // un-declare field
                    )
                )
                .writeTo(defaultTestPath);
        } catch (ExcelWriteException e) {
            assert true;
        }
    }

    @Test public void testResetMethod() throws IOException {
        new Workbook("重写期末成绩", author)
            .addSheet(new ListSheet<Student>("重写期末成绩", Collections.singletonList(new Student(9527, author, 0) {
                    @Override
                    public int getScore() {
                        return 100;
                    }
                }))
            )
            .writeTo(defaultTestPath);
    }

    @Test public void testMethodAnnotation() throws IOException {
        new Workbook("重写方法注解", author)
            .addSheet(new ListSheet<Student>("重写方法注解", Collections.singletonList(new ExtStudent(9527, author, 0) {
                @Override
                @ExcelColumn("ID")
                public int getId() {
                    return super.getId();
                }

                @Override
                @ExcelColumn("SCORE")
                public int getScore() {
                    return 97;
                }
            }))
            )
            .writeTo(defaultTestPath);

        try (ExcelReader reader = ExcelReader.read(defaultTestPath.resolve("重写方法注解.xlsx"))) {
            Optional<ExtStudent> opt = reader.sheets().flatMap(org.ttzero.excel.reader.Sheet::dataRows)
                .map(row -> row.too(ExtStudent.class)).findAny();
            assert opt.isPresent();
            ExtStudent student = opt.get();
            assert student.getId() == 9527;
            assert student.getScore() == 97;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test public void testIgnoreSupperMethod() throws IOException {
        new Workbook("忽略父类属性", author)
            .addSheet(new ListSheet<Student>("重写方法注解", Collections.singletonList(new ExtStudent(9527, author, 0))))
            .writeTo(defaultTestPath);

        try (ExcelReader reader = ExcelReader.read(defaultTestPath.resolve("忽略父类属性.xlsx"))) {
            Optional<ExtStudent> opt = reader.sheets().flatMap(org.ttzero.excel.reader.Sheet::dataRows)
                .map(row -> row.too(ExtStudent.class)).findAny();
            assert opt.isPresent();
            ExtStudent student = opt.get();
            assert student.getId() == 0;
            assert student.getScore() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Issue #93
    @Test public void testListSheet_93() throws IOException {
        new Workbook("Issue#93 List Object").addSheet(new ListSheet<Student>() {
            private int i;
            @Override
            protected List<Student> more() {
                return i++ < 10 ? Student.randomTestData(100) : null;
            }
        }).writeTo(defaultTestPath);
    }

    // Issue #95
    @Test public void testIssue_95() throws IOException {
        new Workbook("Issue #95").addSheet(new ListSheet<NotSharedObject>() {
            private boolean c = true;
            @Override
            protected List<NotSharedObject> more() {
                if (!c) return null;
                c = false;
                List<NotSharedObject> list = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    list.add(new NotSharedObject(getRandomString()));
                }
                return list;
            }
        }).writeTo(defaultTestPath);
    }

    @Test public void testSpecifyCore() throws IOException {
        Core core = new Core();
        core.setCreator("一名光荣的测试人员");
        core.setTitle("空白文件");
        core.setSubject("主题");
        core.setCategory("IT;木工");
        core.setDescription("为了艾尔");
        core.setKeywords("机枪兵;光头");
        core.setVersion("1.0");
        core.setRevision("1.2");
        core.setLastModifiedBy("TTT");
        new Workbook("Specify Core")
            .setCore(core)
            .addSheet(new ListSheet<>(Collections.singletonList(new NotSharedObject(getRandomString()))))
                .writeTo(defaultTestPath);
    }

    @Test public void testLarge() throws IOException {
        new Workbook("large07").addSheet(new ListSheet<ExcelReaderTest.LargeData>() {
            private int i = 0, n;

            @Override
            public List<ExcelReaderTest.LargeData> more() {
                if (n++ >= 10) return null;
                List<ExcelReaderTest.LargeData> list = new ArrayList<>();
                int size = i + 5000;
                for (; i < size; i++) {
                    ExcelReaderTest.LargeData largeData = new ExcelReaderTest.LargeData();
                    list.add(largeData);
                    largeData.setStr1("str1-" + i);
                    largeData.setStr2("str2-" + i);
                    largeData.setStr3("str3-" + i);
                    largeData.setStr4("str4-" + i);
                    largeData.setStr5("str5-" + i);
                    largeData.setStr6("str6-" + i);
                    largeData.setStr7("str7-" + i);
                    largeData.setStr8("str8-" + i);
                    largeData.setStr9("str9-" + i);
                    largeData.setStr10("str10-" + i);
                    largeData.setStr11("str11-" + i);
                    largeData.setStr12("str12-" + i);
                    largeData.setStr13("str13-" + i);
                    largeData.setStr14("str14-" + i);
                    largeData.setStr15("str15-" + i);
                    largeData.setStr16("str16-" + i);
                    largeData.setStr17("str17-" + i);
                    largeData.setStr18("str18-" + i);
                    largeData.setStr19("str19-" + i);
                    largeData.setStr20("str20-" + i);
                    largeData.setStr21("str21-" + i);
                    largeData.setStr22("str22-" + i);
                    largeData.setStr23("str23-" + i);
                    largeData.setStr24("str24-" + i);
                    largeData.setStr25("str25-" + i);
                }
                return list;
            }
        }).writeTo(defaultTestPath);
    }

    // #132
    @Test public void testEmptyList() throws IOException {
        new Workbook().addSheet(new ListSheet<>(new ArrayList<>())).writeTo(defaultTestPath);
    }

    public static class Item {
        private int id;
        private String name;

        public Item(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public static List<Item> randomTestData(int n) {
            List<Item> list = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                list.add(new Item(i, getRandomString()));
            }
            return list;
        }

        public static List<Item> randomTestData() {
            int n = random.nextInt(100) + 1;
            return randomTestData(n);
        }
    }

    public static class AllType {
        private boolean bv;
        private char cv;
        private short sv;
        private int nv;
        private long lv;
        private float fv;
        private double dv;
        private String s;
        private BigDecimal mv;
        private Date av;
        private Timestamp iv;
        private Time tv;
        private LocalDate ldv;
        private LocalDateTime ldtv;
        private LocalTime ltv;

        public static List<AllType> randomTestData(int size) {
            List<AllType> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                AllType o = new AllType();
                o.bv = random.nextInt(10) == 5;
                o.cv = charArray[random.nextInt(charArray.length)];
                o.sv = (short) (random.nextInt() & 0xFFFF);
                o.nv = random.nextInt();
                o.lv = random.nextLong();
                o.fv = random.nextFloat();
                o.dv = random.nextDouble();
                o.s = getRandomString();
                o.mv = BigDecimal.valueOf(random.nextDouble());
                o.av = new Date();
                o.iv = new Timestamp(System.currentTimeMillis() - random.nextInt(9999999));
                o.tv = new Time(random.nextLong());
                o.ldv = LocalDate.now();
                o.ldtv = LocalDateTime.now();
                o.ltv = LocalTime.now();
                list.add(o);
            }
            return list;
        }

        public static List<AllType> randomTestData() {
            int size = random.nextInt(100) + 1;
            return randomTestData(size);
        }

        public boolean isBv() {
            return bv;
        }

        public char getCv() {
            return cv;
        }

        public short getSv() {
            return sv;
        }

        public int getNv() {
            return nv;
        }

        public long getLv() {
            return lv;
        }

        public float getFv() {
            return fv;
        }

        public double getDv() {
            return dv;
        }

        public String getS() {
            return s;
        }

        public BigDecimal getMv() {
            return mv;
        }

        public Date getAv() {
            return av;
        }

        public Timestamp getIv() {
            return iv;
        }

        public Time getTv() {
            return tv;
        }

        public LocalDate getLdv() {
            return ldv;
        }

        public LocalDateTime getLdtv() {
            return ldtv;
        }

        public LocalTime getLtv() {
            return ltv;
        }

        public void setBv(boolean bv) {
            this.bv = bv;
        }

        public void setCv(char cv) {
            this.cv = cv;
        }

        public void setSv(short sv) {
            this.sv = sv;
        }

        public void setNv(int nv) {
            this.nv = nv;
        }

        public void setLv(long lv) {
            this.lv = lv;
        }

        public void setFv(float fv) {
            this.fv = fv;
        }

        public void setDv(double dv) {
            this.dv = dv;
        }

        public void setS(String s) {
            this.s = s;
        }

        public void setMv(BigDecimal mv) {
            this.mv = mv;
        }

        public void setAv(Date av) {
            this.av = av;
        }

        public void setIv(Timestamp iv) {
            this.iv = iv;
        }

        public void setTv(Time tv) {
            this.tv = tv;
        }

        public void setLdv(LocalDate ldv) {
            this.ldv = ldv;
        }

        public void setLdtv(LocalDateTime ldtv) {
            this.ldtv = ldtv;
        }

        public void setLtv(LocalTime ltv) {
            this.ltv = ltv;
        }

        @Override
        public String toString() {
            return "" + bv + '|' + cv + '|' + sv + '|' + nv + '|' + lv
                + '|' + fv + '|' + dv + '|' + s + '|' + mv + '|' + av
                + '|' + tv + '|' + ldv + '|' + ldtv + '|' + ltv;
        }
    }

    /**
     * Annotation Object
     */
    public static class Student {
        @IgnoreExport
        private int id;
        @ExcelColumn("姓名")
        private String name;
        @ExcelColumn("成绩")
        private int score;

        public Student() { }

        protected Student(int id, String name, int score) {
            this.id = id;
            this.name = name;
            this.score = score;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public static List<Student> randomTestData(int pageNo, int limit) {
            List<Student> list = new ArrayList<>(limit);
            for (int i = pageNo * limit, n = i + limit; i < n; i++) {
                Student e = new Student(i, getRandomString(), random.nextInt(50) + 50);
                list.add(e);
            }
            return list;
        }

        public static List<Student> randomTestData(int n) {
            return randomTestData(0, n);
        }

        public static List<Student> randomTestData() {
            int n = random.nextInt(100) + 1;
            return randomTestData(n);
        }

        @Override
        @ExcelColumn
        public String toString() {
            return "id: " + id + ", name: " + name + ", score: " + score;
        }
    }

    public static class BoxAllType {
        private Boolean bv;
        private Character cv;
        private Short sv;
        private Integer nv;
        private Long lv;
        private Float fv;
        private Double dv;
        private String s;
        private BigDecimal mv;
        private Date av;
        private Timestamp iv;
        private Time tv;
        private LocalDate ldv;
        private LocalDateTime ldtv;
        private LocalTime ltv;

        public static List<AllType> randomTestData(int size) {
            List<AllType> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                AllType o = new AllType();
                o.bv = random.nextInt(10) == 5;
                o.cv = charArray[random.nextInt(charArray.length)];
                o.sv = (short) (random.nextInt() & 0xFFFF);
                o.nv = random.nextInt();
                o.lv = random.nextLong();
                o.fv = random.nextFloat();
                o.dv = random.nextDouble();
                o.s = getRandomString();
                o.mv = BigDecimal.valueOf(random.nextDouble());
                o.av = new Date();
                o.iv = new Timestamp(System.currentTimeMillis() - random.nextInt(9999999));
                o.tv = new Time(random.nextLong());
                o.ldv = LocalDate.now();
                o.ldtv = LocalDateTime.now();
                o.ltv = LocalTime.now();
                list.add(o);
            }
            return list;
        }

        public static List<AllType> randomTestData() {
            int size = random.nextInt(100) + 1;
            return randomTestData(size);
        }

        public Boolean getBv() {
            return bv;
        }

        public Character getCv() {
            return cv;
        }

        public Short getSv() {
            return sv;
        }

        public Integer getNv() {
            return nv;
        }

        public Long getLv() {
            return lv;
        }

        public Float getFv() {
            return fv;
        }

        public Double getDv() {
            return dv;
        }

        public String getS() {
            return s;
        }

        public BigDecimal getMv() {
            return mv;
        }

        public Date getAv() {
            return av;
        }

        public Timestamp getIv() {
            return iv;
        }

        public Time getTv() {
            return tv;
        }

        public LocalDate getLdv() {
            return ldv;
        }

        public LocalDateTime getLdtv() {
            return ldtv;
        }

        public LocalTime getLtv() {
            return ltv;
        }

        public void setBv(Boolean bv) {
            this.bv = bv;
        }

        public void setCv(Character cv) {
            this.cv = cv;
        }

        public void setSv(Short sv) {
            this.sv = sv;
        }

        public void setNv(Integer nv) {
            this.nv = nv;
        }

        public void setLv(Long lv) {
            this.lv = lv;
        }

        public void setFv(Float fv) {
            this.fv = fv;
        }

        public void setDv(Double dv) {
            this.dv = dv;
        }

        public void setS(String s) {
            this.s = s;
        }

        public void setMv(BigDecimal mv) {
            this.mv = mv;
        }

        public void setAv(Date av) {
            this.av = av;
        }

        public void setIv(Timestamp iv) {
            this.iv = iv;
        }

        public void setTv(Time tv) {
            this.tv = tv;
        }

        public void setLdv(LocalDate ldv) {
            this.ldv = ldv;
        }

        public void setLdtv(LocalDateTime ldtv) {
            this.ldtv = ldtv;
        }

        public void setLtv(LocalTime ltv) {
            this.ltv = ltv;
        }

        @Override
        public String toString() {
            return "" + bv + '|' + cv + '|' + sv + '|' + nv + '|' + lv
                + '|' + fv + '|' + dv + '|' + s + '|' + mv + '|' + av
                + '|' + tv + '|' + ldv + '|' + ldtv + '|' + ltv;
        }
    }

    public static class ExtItem extends Item {
        private String nice;

        public ExtItem(int id, String name) {
            super(id, name);
        }

        public String getNice() {
            return nice;
        }

        public void setNice(String nice) {
            this.nice = nice;
        }

        public static List<Item> randomTestData(int n) {
            List<Item> list = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                list.add(new ExtItem(i,  getRandomString()));
            }
            return list;
        }
    }

    public static class NotSharedObject {
        @ExcelColumn(share = false)
        private String name;

        public NotSharedObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ExtStudent extends Student {
        public ExtStudent() { }
        protected ExtStudent(int id, String name, int score) {
            super(id, name, score);
        }

        @Override
        @ExcelColumn("ID")
        @IgnoreExport
        public int getId() {
            return super.getId();
        }

        @ExcelColumn("ID")
        @Override
        public void setId(int id) {
            super.setId(id);
        }

        @Override
        @ExcelColumn("SCORE")
        @IgnoreExport
        public int getScore() {
            return super.getScore();
        }

        @ExcelColumn("SCORE")
        @Override
        public void setScore(int score) {
            super.setScore(score);
        }
    }
}
