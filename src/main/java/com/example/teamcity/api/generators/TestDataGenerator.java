package com.example.teamcity.api.generators;

import com.example.teamcity.api.annotations.Optional;
import com.example.teamcity.api.annotations.Parameterizable;
import com.example.teamcity.api.annotations.Random;
import com.example.teamcity.api.models.BaseModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TestDataGenerator {

    private TestDataGenerator() {
    }

    /**
     * Основной метод генерации тестовых данных.
     *
     * Если у поля аннотация Optional, оно пропускается, иначе:
     *
     * 1) если у поля аннотация Parameterizable, и в метод были переданы параметры, то поочередно (по мере встречи полей с
     *     этой аннотацией) устанавливаются переданные параметры. То есть, если по ходу генерации было пройдено 4 поля с
     *     аннотацией Parameterizable, но параметров в метод было передано 3, то значения будут установлены только у первых
     *     трех встретившихся элементов в порядке их передачи в метод. Поэтому также важно следить за порядком полей
     *     в @Data классе;
     *
     * 2) иначе, если у поля аннотация Random и это строка, оно заполняется рандомными данными;
     *
     * 3) иначе, если поле - наследник класса BaseModel, то оно генерируется, рекурсивно отправляясь в новый метод generate;
     *
     * 4) иначе, если поле - List, у которого generic type - наследник класса BaseModel, то оно устанавливается списком
     *     из одного элемента, который генерируется, рекурсивно отправляясь в новый метод generate.
     *
     * Параметр generatedModels передается, когда генерируется несколько сущностей в цикле, и содержит в себе
     * сгенерированные на предыдущих шагах сущности. Позволяет при генерации сложной сущности, которая своим полем содержит
     * другую сущность, сгенерированную на предыдущем шаге, установить ее, а не генерировать новую. Данная логика
     * применяется только для пунктов 3 и 4. Например, если был сгенерирован NewProjectDescription, то передав его
     * параметром generatedModels при генерации BuildType, он будет переиспользоваться при установке
     * поля NewProjectDescription project, вместо генерации нового.
     */
    public static <T extends BaseModel> T generate(List<BaseModel> generatedModels, Class<T> generatorClass,
                                                   Object... parameters) {
        try {
            //Создание нового экземпляра класса generatorClass с использованием конструктора по умолчанию
            var instance = generatorClass.getDeclaredConstructor().newInstance();

            //Перебор всех полей класса generatorClass
            for (var field : generatorClass.getDeclaredFields()) {
                //Делаем приватные поля доступными
                field.setAccessible(true);

                //Проверяем что поле не помечено как не обязательное (@Optional)
                if (!field.isAnnotationPresent(Optional.class)) {
                    // Пытаемся найти ранее сгенерированную модель того же типа, что и поле
                    var generatedClass = generatedModels.stream().filter(m
                            -> m.getClass().equals(field.getType())).findFirst();
                    // Проверяем, можно ли параметризировать поле, и доступны ли параметры
                    if (field.isAnnotationPresent(Parameterizable.class) && parameters.length > 0) {
                        // Устанавливаем поле с первым параметром и обновлем массив параметров
                        field.set(instance, parameters[0]);
                        parameters = Arrays.copyOfRange(parameters, 1, parameters.length);

                        // Проверяем, должны ли мы заполнять поле случайными данными
                    } else if (field.isAnnotationPresent(Random.class)) {
                        // Устанавливаем поле случайной строкой
                        if (String.class.equals(field.getType())) {
                            field.set(instance, RandomData.getString());
                            // Или случайным числом
                        } else if (Integer.class.equals(field.getType())) {
                            field.set(instance, RandomData.getInteger());
                        }

                        // Обрабатываем инициализацию полей, являющихся подклассами BaseModel
                    } else if (BaseModel.class.isAssignableFrom(field.getType())) {
                        var finalParameters = parameters;
                        // Рекурсивно генерируем экземпляр для этого поля или используем найденный экземпляр
                        field.set(instance, generatedClass.orElseGet(() -> generate(
                                generatedModels, field.getType().asSubclass(BaseModel.class), finalParameters)));

                        // Обрабатываем поля, являющиеся списками подклассов BaseModel
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        if (field.getGenericType() instanceof ParameterizedType pt) {
                            var typeClass = (Class<?>) pt.getActualTypeArguments()[0];
                            if (BaseModel.class.isAssignableFrom(typeClass)) {
                                var finalParameters = parameters;
                                // Рекурсивно генерируем список экземпляров или используем найденные экземпляры
                                field.set(instance, generatedClass.map(List::of).orElseGet(() -> List.of(generate(
                                        generatedModels, typeClass.asSubclass(BaseModel.class), finalParameters))));
                            }
                        }
                    }
                }
                // Возвращаем изменения доступности поля в исходное состояние
                field.setAccessible(false);
            }
            // Возвращаем полностью инициализированный и заполненный экземпляр
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot generate test data", e);
        }
    }

    // Метод, чтобы сгенерировать одну сущность. Передает пустой параметр generatedModels
    public static <T extends BaseModel> T generate(Class<T> generatorClass, Object... parameters) {
        return generate(Collections.emptyList(), generatorClass, parameters);
    }
}
