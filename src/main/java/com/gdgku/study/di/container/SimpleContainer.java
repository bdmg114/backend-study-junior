package com.gdgku.study.di.container;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * [2단계: 수제 DI 컨테이너]
 *
 * 스프링 없이 "이 타입은 이 구현체를 쓴다"는 매핑만 등록해두면, 실제 조립(생성자 호출,
 * 의존성 연결)은 이 컨테이너가 리플렉션으로 대신 해준다. 어노테이션은 전혀 쓰지 않는다 -
 * register()로 넘긴 타입 정보만으로 동작한다.
 *
 * 스프링의 ApplicationContext가 내부적으로 하는 일(타입 기반 빈 등록 + 생성자 파라미터를
 * 재귀적으로 resolve해서 조립 + 싱글턴 캐싱)을 아주 작게 흉내낸 것이다.
 *
 * 한계: 생성자가 여러 개인 클래스, 순환 의존성, 인터페이스 중복 등록(둘 이상의 구현체) 같은
 * 상황은 전혀 처리하지 못한다. 실제 스프링 컨테이너는 이런 문제들까지 해결해준다.
 */
public class SimpleContainer {

    private final Map<Class<?>, Class<?>> registrations = new HashMap<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();

    public <T> void register(Class<T> type, Class<? extends T> implementation) {
        registrations.put(type, implementation);
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type) {
        if (singletons.containsKey(type)) {
            return (T) singletons.get(type);
        }

        Class<?> implementation = registrations.get(type);
        if (implementation == null) {
            throw new IllegalStateException(type.getName() + " 타입이 컨테이너에 등록되어 있지 않습니다.");
        }

        Constructor<?> constructor = implementation.getConstructors()[0];
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] dependencies = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            dependencies[i] = resolve(parameterTypes[i]);
        }

        try {
            T instance = (T) constructor.newInstance(dependencies);
            singletons.put(type, instance);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(implementation.getName() + " 인스턴스 생성에 실패했습니다.", e);
        }
    }
}
