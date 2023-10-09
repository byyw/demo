package com.byyw.demo.plus;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventBusPlus extends EventBus {
    private Map<Object, Set<String>> map;

    public EventBusPlus() {
        this.map = new HashMap<>();
    }

    public void register(Object obj) {
        Method[] ms = obj.getClass().getDeclaredMethods();
        for (Method m : ms) {
            if (m.getAnnotation(Subscribe.class) != null) {
                if (m.getAnnotation(EventParam.class) != null) {
                    // 注解了EventParam，则只会接收到EventParam.value()的事件
                    EventParam ep = m.getAnnotation(EventParam.class);
                    Parameter p = m.getParameters()[0];
                    if (!map.containsKey(p.getType())) {
                        map.put(p.getType(), new HashSet<>());
                    }
                    map.get(p.getType()).add(ep.value());
                } else {
                    // 没注解了EventParam，默认“*”，可接收到任意事件
                    Parameter p = m.getParameters()[0];
                    if (!map.containsKey(p.getType())) {
                        map.put(p.getType(), new HashSet<>());
                    }
                    map.get(p.getType()).add("*");
                }
            }
        }
        // eventBus.register(obj);
        invoke("subscribers", "register", new Class[] { Object.class }, obj);
    }

    public void post(Object event, String key) {

        if (!(map.containsKey(event.getClass())
                && (map.get(event.getClass()).contains(key) || map.get(event.getClass()).contains("*")))) {
            return;
        }

        // Iterator<Subscriber> eventSubscribers = subscribers.getSubscribers(event);
        Iterator eventSubscribers = (Iterator) invoke("subscribers", "getSubscribers",
                new Class[] { Object.class }, event);

        List subscriberIterators = new ArrayList<>();
        while (eventSubscribers.hasNext()) {
            Object subscriber = eventSubscribers.next();
            try {
                Field method = subscriber.getClass().getDeclaredField("method");
                method.setAccessible(true);
                Method methodObj = (Method) method.get(subscriber);
                EventParam ep = methodObj.getAnnotation(EventParam.class);
                if (ep == null || ep.value().equals(key)) {
                    subscriberIterators.add(subscriber);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (subscriberIterators.size() != 0) {
            // dispatcher.dispatch(event, eventSubscribers);
            invoke("dispatcher", "dispatch", new Class[] { Object.class, Iterator.class }, event,
                    subscriberIterators.iterator());
        } else if (!(event instanceof DeadEvent)) {
            post(new DeadEvent(this, event));
        }

    }

    public void post(Object event) {
        post(event, null);
    }

    private Object invoke(String field, String method, Class[] parameterTypes, Object... args) {
        try {
            Field subscribers = this.getClass().getSuperclass().getDeclaredField(field);
            subscribers.setAccessible(true);
            Method getSubscribers = subscribers.getType().getDeclaredMethod(method, parameterTypes);
            getSubscribers.setAccessible(true);
            return getSubscribers.invoke(subscribers.get(this), args);
        } catch (NoSuchFieldException | SecurityException | NoSuchMethodException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
