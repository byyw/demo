package com.byyw.demo.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳保活机制管理
 */
@Slf4j
@Component
public class HeartManager {

    private ConcurrentMap<String, Cell> map; // 对象id-对象 映射
    private Cell head; // 队首
    private Cell tail; // 队尾
    private long date = System.currentTimeMillis(); // 当前时间

    @PostConstruct
    public void construct() {
        head = new Cell("head", -1);
        tail = new Cell("tail", -1);
        head.setLast(tail);
        tail.setFront(head);
        map = new ConcurrentHashMap<String, Cell>();

        new Thread(() -> {
            while (true) {
                date = System.currentTimeMillis();
                Cell c = tail.getFront();
                if (c == null)
                    return;
                while (true) {
                    if (c.getLimited() == -1 || date - c.getLastTime() < (c.getLimited() + 1)) {
                        break;
                    }
                    c = c.getFront();
                    remove(c.getLast()).e.excite();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 添加对象
     * 
     * @param id
     * @param limited ms
     * @param e
     * @throws Exception
     */
    public synchronized void add(String id, int limited, OvertimeEvent e) {
        if (map.containsKey(id)) {
            refresh(id);
        }
        Cell c = new Cell(id, limited, e);
        c.setLast(head.getLast());
        c.setFront(head);
        head.getLast().setFront(c);
        head.setLast(c);
        map.put(id, c);
    }

    // 移除对象
    public void remove(String id) {
        if (map.containsKey(id))
            this.remove(map.get(id));
    }

    public synchronized Cell remove(Cell c) {
        c.getFront().setLast(c.getLast());
        c.getLast().setFront(c.getFront());
        c.setFront(null);
        c.setLast(null);
        map.remove(c.getId());
        return c;
    }

    // 更新应答时间，并将对象移至队首
    public synchronized void refresh(String id) {
        Cell c = map.get(id);
        if (c == null)
            return;
        c.setLastTime(date);
        c.getFront().setLast(c.getLast());
        c.getLast().setFront(c.getFront());

        c.setLast(head.getLast());
        c.setFront(head);
        head.getLast().setFront(c);
        head.setLast(c);
    }

    public boolean hasExist(String id) {
        return map.containsKey(id);
    }

    public Integer size(){
        return map.size();
    }

    @Data
    public class Cell {
        private Cell front;
        private Cell last;

        private String id; // 服务id
        private int limited; // 超时时间
        private long lastTime; // 上次应答时间
        public OvertimeEvent e;

        public Cell(String id, int limited) {
            this(id, limited, () -> {
            });
        }

        public Cell(String id, int limited, OvertimeEvent e) {
            this.id = id;
            this.limited = limited;
            this.lastTime = date;
            this.e = e;
        }
    }

    public interface OvertimeEvent {
        void excite();
    }
}
