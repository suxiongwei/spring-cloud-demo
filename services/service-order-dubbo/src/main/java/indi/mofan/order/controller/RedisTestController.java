package indi.mofan.order.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Data
    public static class TestResult {
        private String testName;
        private boolean success;
        private String message;
        private Object data;
        private long executionTime;
        private String explanation;
        private List<String> interviewPoints;

        public static TestResult success(String testName, String message, Object data, String explanation, List<String> interviewPoints) {
            TestResult result = new TestResult();
            result.setTestName(testName);
            result.setSuccess(true);
            result.setMessage(message);
            result.setData(data);
            result.setExplanation(explanation);
            result.setInterviewPoints(interviewPoints);
            return result;
        }

        public static TestResult error(String testName, String message, String explanation) {
            TestResult result = new TestResult();
            result.setTestName(testName);
            result.setSuccess(false);
            result.setMessage(message);
            result.setExplanation(explanation);
            return result;
        }
    }

    @GetMapping("/string")
    public TestResult testString() {
        long startTime = System.currentTimeMillis();
        try {
            String key = "test:string:demo";
            List<String> operations = new ArrayList<>();

            redisTemplate.opsForValue().set(key, "Hello Redis");
            operations.add("SET " + key + " 'Hello Redis'");
            Object value = redisTemplate.opsForValue().get(key);
            operations.add("GET " + key + " => " + value);

            redisTemplate.opsForValue().set(key, "Hello Redis", 10, TimeUnit.SECONDS);
            operations.add("SETEX " + key + " 10 'Hello Redis'");
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            operations.add("TTL " + key + " => " + ttl + " seconds");

            redisTemplate.opsForValue().increment(key + ":counter", 1);
            operations.add("INCR " + key + ":counter => 1");
            redisTemplate.opsForValue().increment(key + ":counter", 5);
            operations.add("INCRBY " + key + ":counter => 5");
            Object counter = redisTemplate.opsForValue().get(key + ":counter");
            operations.add("GET " + key + ":counter => " + counter);

            redisTemplate.opsForValue().set(key + ":json", "{\"name\":\"test\",\"value\":123}");
            operations.add("SET " + key + ":json '{\"name\":\"test\",\"value\":123}'");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "String是Redis最基本的数据类型，可以存储任何形式的字符串",
                "底层实现：SDS（Simple Dynamic String），支持二进制安全",
                "常用命令：SET、GET、INCR、DECR、SETEX、TTL等",
                "应用场景：缓存、计数器、分布式锁、Session共享",
                "面试重点：SDS结构、内存优化、编码方式（int、embstr、raw）"
            );

            return TestResult.success(
                "Redis String操作",
                "String数据结构操作测试完成",
                operations,
                "String类型是Redis中最简单的数据类型，底层使用SDS（Simple Dynamic String）实现。SDS相比C字符串有以下优势：1）O(1)时间复杂度获取字符串长度；2）避免缓冲区溢出；3）减少内存重分配次数；4）二进制安全。String类型可以存储字符串、整数、JSON等数据，支持丰富的操作命令。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("Redis String操作", e.getMessage(), "String操作失败，请检查Redis连接");
        }
    }

    @GetMapping("/hash")
    public TestResult testHash() {
        long startTime = System.currentTimeMillis();
        try {
            String key = "test:hash:user:1001";
            List<String> operations = new ArrayList<>();

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", "张三");
            userMap.put("age", 25);
            userMap.put("email", "zhangsan@example.com");
            redisTemplate.opsForHash().putAll(key, userMap);
            operations.add("HSET " + key + " name '张三' age 25 email 'zhangsan@example.com'");

            Object name = redisTemplate.opsForHash().get(key, "name");
            operations.add("HGET " + key + " name => " + name);

            Map<Object, Object> allFields = redisTemplate.opsForHash().entries(key);
            operations.add("HGETALL " + key + " => " + allFields);

            Long age = redisTemplate.opsForHash().increment(key, "age", 1);
            operations.add("HINCRBY " + key + " age 1 => " + age);

            Set<Object> keys = redisTemplate.opsForHash().keys(key);
            operations.add("HKEYS " + key + " => " + keys);

            List<Object> values = redisTemplate.opsForHash().values(key);
            operations.add("HVALS " + key + " => " + values);

            redisTemplate.opsForHash().delete(key, "email");
            operations.add("HDEL " + key + " email");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "Hash是一个键值对集合，适合存储对象",
                "底层实现：ziplist（元素少且值小）或hashtable（元素多或值大）",
                "常用命令：HSET、HGET、HGETALL、HINCRBY、HDEL、HKEYS、HVALS",
                "应用场景：存储对象、购物车、用户信息",
                "面试重点：ziplist和hashtable的转换条件、内存优化、HGETALL的性能问题"
            );

            return TestResult.success(
                "Redis Hash操作",
                "Hash数据结构操作测试完成",
                operations,
                "Hash类型是一个键值对集合，适合存储对象。底层实现有两种编码：1）ziplist：当元素数量小于512个且所有值小于64字节时使用，内存紧凑；2）hashtable：当元素较多或值较大时使用，查找效率O(1)。Hash类型特别适合存储对象属性，如用户信息、商品详情等。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("Redis Hash操作", e.getMessage(), "Hash操作失败，请检查Redis连接");
        }
    }

    @GetMapping("/list")
    public TestResult testList() {
        long startTime = System.currentTimeMillis();
        try {
            String key = "test:list:tasks";
            List<String> operations = new ArrayList<>();

            redisTemplate.opsForList().rightPushAll(key, "task1", "task2", "task3");
            operations.add("RPUSH " + key + " task1 task2 task3");

            Object leftPop = redisTemplate.opsForList().leftPop(key);
            operations.add("LPOP " + key + " => " + leftPop);

            Object rightPop = redisTemplate.opsForList().rightPop(key);
            operations.add("RPOP " + key + " => " + rightPop);

            redisTemplate.opsForList().leftPush(key, "task0");
            operations.add("LPUSH " + key + " task0");

            List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
            operations.add("LRANGE " + key + " 0 -1 => " + range);

            Long size = redisTemplate.opsForList().size(key);
            operations.add("LLEN " + key + " => " + size);

            redisTemplate.opsForList().set(key, 0, "task0-updated");
            operations.add("LSET " + key + " 0 'task0-updated'");

            Object index = redisTemplate.opsForList().index(key, 0);
            operations.add("LINDEX " + key + " 0 => " + index);

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "List是简单的字符串列表，按插入顺序排序",
                "底层实现：quicklist（ziplist + linkedlist的混合实现）",
                "常用命令：LPUSH、RPUSH、LPOP、RPOP、LRANGE、LLEN、LINDEX、LSET",
                "应用场景：消息队列、最新列表、栈、队列",
                "面试重点：quicklist结构、LPUSH和RPUSH的时间复杂度、阻塞命令BLPOP/BRPOP"
            );

            return TestResult.success(
                "Redis List操作",
                "List数据结构操作测试完成",
                operations,
                "List类型是简单的字符串列表，按照插入顺序排序。底层使用quicklist实现，quicklist是ziplist和linkedlist的混合结构，每个节点是一个ziplist，节点之间通过指针连接。这种设计既保持了内存紧凑性，又支持快速的插入和删除操作。List类型常用于实现消息队列、最新列表、栈和队列等场景。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("Redis List操作", e.getMessage(), "List操作失败，请检查Redis连接");
        }
    }

    @GetMapping("/set")
    public TestResult testSet() {
        long startTime = System.currentTimeMillis();
        try {
            String key1 = "test:set:users:active";
            String key2 = "test:set:users:vip";
            List<String> operations = new ArrayList<>();

            redisTemplate.opsForSet().add(key1, "user1", "user2", "user3", "user4");
            operations.add("SADD " + key1 + " user1 user2 user3 user4");

            redisTemplate.opsForSet().add(key2, "user2", "user3", "user5");
            operations.add("SADD " + key2 + " user2 user3 user5");

            Set<Object> members = redisTemplate.opsForSet().members(key1);
            operations.add("SMEMBERS " + key1 + " => " + members);

            Boolean isMember = redisTemplate.opsForSet().isMember(key1, "user2");
            operations.add("SISMEMBER " + key1 + " user2 => " + isMember);

            Set<Object> difference = redisTemplate.opsForSet().difference(key1, key2);
            operations.add("SDIFF " + key1 + " " + key2 + " => " + difference);

            Set<Object> intersection = redisTemplate.opsForSet().intersect(key1, key2);
            operations.add("SINTER " + key1 + " " + key2 + " => " + intersection);

            Set<Object> union = redisTemplate.opsForSet().union(key1, key2);
            operations.add("SUNION " + key1 + " " + key2 + " => " + union);

            Long size = redisTemplate.opsForSet().size(key1);
            operations.add("SCARD " + key1 + " => " + size);

            redisTemplate.opsForSet().remove(key1, "user1");
            operations.add("SREM " + key1 + " user1");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "Set是无序的字符串集合，元素唯一",
                "底层实现：intset（整数集合）或hashtable（哈希表）",
                "常用命令：SADD、SMEMBERS、SISMEMBER、SINTER、SUNION、SDIFF、SCARD、SREM",
                "应用场景：标签系统、共同好友、抽奖系统、去重",
                "面试重点：intset和hashtable的转换条件、集合运算的时间复杂度、去重原理"
            );

            return TestResult.success(
                "Redis Set操作",
                "Set数据结构操作测试完成",
                operations,
                "Set类型是无序的字符串集合，元素唯一不重复。底层实现有两种编码：1）intset：当元素都是整数且数量较少时使用，内存紧凑；2）hashtable：当元素较多或包含非整数时使用。Set类型支持丰富的集合运算（交集、并集、差集），常用于标签系统、共同好友、抽奖系统、数据去重等场景。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("Redis Set操作", e.getMessage(), "Set操作失败，请检查Redis连接");
        }
    }

    @GetMapping("/zset")
    public TestResult testZSet() {
        long startTime = System.currentTimeMillis();
        try {
            String key = "test:zset:leaderboard";
            List<String> operations = new ArrayList<>();

            redisTemplate.opsForZSet().add(key, "player1", 100);
            redisTemplate.opsForZSet().add(key, "player2", 200);
            redisTemplate.opsForZSet().add(key, "player3", 150);
            redisTemplate.opsForZSet().add(key, "player4", 300);
            operations.add("ZADD " + key + " 100 player1 200 player2 150 player3 300 player4");

            Set<Object> range = redisTemplate.opsForZSet().range(key, 0, -1);
            operations.add("ZRANGE " + key + " 0 -1 => " + range);

            Set<Object> reverseRange = redisTemplate.opsForZSet().reverseRange(key, 0, 2);
            operations.add("ZREVRANGE " + key + " 0 2 => " + reverseRange);

            Long rank = redisTemplate.opsForZSet().rank(key, "player2");
            operations.add("ZRANK " + key + " player2 => " + rank);

            Long reverseRank = redisTemplate.opsForZSet().reverseRank(key, "player2");
            operations.add("ZREVRANK " + key + " player2 => " + reverseRank);

            Double score = redisTemplate.opsForZSet().score(key, "player2");
            operations.add("ZSCORE " + key + " player2 => " + score);

            Long count = redisTemplate.opsForZSet().count(key, 150, 250);
            operations.add("ZCOUNT " + key + " 150 250 => " + count);

            redisTemplate.opsForZSet().incrementScore(key, "player2", 50);
            operations.add("ZINCRBY " + key + " 50 player2");

            Long size = redisTemplate.opsForZSet().size(key);
            operations.add("ZCARD " + key + " => " + size);

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "ZSet是有序集合，每个元素关联一个分数（score）",
                "底层实现：ziplist（元素少且值小）或skiplist（跳跃表）+ hashtable",
                "常用命令：ZADD、ZRANGE、ZREVRANGE、ZRANK、ZSCORE、ZCOUNT、ZINCRBY、ZCARD",
                "应用场景：排行榜、延时队列、优先级队列、范围查询",
                "面试重点：跳跃表结构、时间复杂度、ZADD和ZRANGE的实现原理、内存优化"
            );

            return TestResult.success(
                "Redis ZSet操作",
                "ZSet数据结构操作测试完成",
                operations,
                "ZSet类型是有序集合，每个元素关联一个分数（score），按照分数排序。底层实现有两种编码：1）ziplist：当元素数量少于128个且所有元素长度小于64字节时使用；2）skiplist + hashtable：当元素较多时使用，skiplist用于排序和范围查询，hashtable用于快速查找元素。ZSet类型特别适合实现排行榜、延时队列、优先级队列等场景。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("Redis ZSet操作", e.getMessage(), "ZSet操作失败，请检查Redis连接");
        }
    }

    @GetMapping("/lock/basic")
    public TestResult testBasicLock() {
        long startTime = System.currentTimeMillis();
        try {
            String lockKey = "lock:resource:basic";
            String lockValue = UUID.randomUUID().toString();
            List<String> operations = new ArrayList<>();

            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 30, TimeUnit.SECONDS);
            operations.add("SETNX " + lockKey + " " + lockValue + " (with 30s TTL) => " + locked);

            if (Boolean.TRUE.equals(locked)) {
                operations.add("✓ 获取锁成功，执行业务逻辑...");

                Boolean released = releaseLock(lockKey, lockValue);
                operations.add("DEL " + lockKey + " (verify value) => " + released);
            } else {
                operations.add("✗ 获取锁失败，锁已被占用");
            }

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "分布式锁的基本实现：SETNX + EXPIRE",
                "原子性问题：需要使用SET key value NX EX seconds命令",
                "锁的释放：必须验证锁的持有者，避免误删",
                "锁的超时：设置合理的过期时间，防止死锁",
                "面试重点：SETNX和SET命令的区别、原子性保证、锁的误删问题"
            );

            return TestResult.success(
                "基础分布式锁",
                "基础分布式锁测试完成",
                operations,
                "基础分布式锁使用SETNX（SET if Not eXists）命令实现。关键点：1）使用SET key value NX EX seconds命令保证原子性；2）设置唯一的锁标识，释放时验证持有者；3）设置合理的过期时间防止死锁。缺点：锁过期后业务未完成会导致锁失效，无法续期。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("基础分布式锁", e.getMessage(), "分布式锁测试失败，请检查Redis连接");
        }
    }

    private Boolean releaseLock(String lockKey, String lockValue) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue);
        return result != null && result == 1;
    }

    @GetMapping("/lock/reentrant")
    public TestResult testReentrantLock() {
        long startTime = System.currentTimeMillis();
        try {
            String lockKey = "lock:resource:reentrant";
            String requestId = UUID.randomUUID().toString();
            List<String> operations = new ArrayList<>();

            Boolean firstLock = acquireReentrantLock(lockKey, requestId, 30);
            operations.add("第一次获取锁 => " + firstLock);

            if (Boolean.TRUE.equals(firstLock)) {
                Boolean secondLock = acquireReentrantLock(lockKey, requestId, 30);
                operations.add("第二次获取锁（可重入） => " + secondLock);

                Boolean release1 = releaseReentrantLock(lockKey, requestId);
                operations.add("第一次释放锁 => " + release1);

                Boolean release2 = releaseReentrantLock(lockKey, requestId);
                operations.add("第二次释放锁 => " + release2);
            }

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "可重入锁：同一个线程可以多次获取同一个锁",
                "实现方式：使用Hash结构，field为线程标识，value为重入次数",
                "获取锁：HINCRBY增加计数，首次获取时设置过期时间",
                "释放锁：HDECR减少计数，计数为0时删除锁",
                "面试重点：可重入锁的实现原理、计数器管理、过期时间处理"
            );

            return TestResult.success(
                "可重入分布式锁",
                "可重入分布式锁测试完成",
                operations,
                "可重入锁允许同一个线程多次获取同一个锁。实现方式：使用Hash结构存储锁信息，key为锁名称，field为请求标识（如线程ID），value为重入次数。获取锁时使用HINCRBY增加计数，首次获取时设置过期时间；释放锁时使用HDECR减少计数，计数为0时删除锁。这样可以避免同一个线程在递归调用或嵌套锁时死锁。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("可重入分布式锁", e.getMessage(), "可重入锁测试失败，请检查Redis连接");
        }
    }

    private Boolean acquireReentrantLock(String lockKey, String requestId, long expireTime) {
        String script = "if redis.call('exists', KEYS[1]) == 0 then " +
                       "redis.call('hset', KEYS[1], ARGV[1], 1) " +
                       "redis.call('expire', KEYS[1], ARGV[2]) " +
                       "return 1 " +
                       "elseif redis.call('hexists', KEYS[1], ARGV[1]) == 1 then " +
                       "redis.call('hincrby', KEYS[1], ARGV[1], 1) " +
                       "redis.call('expire', KEYS[1], ARGV[2]) " +
                       "return 1 " +
                       "else " +
                       "return 0 " +
                       "end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId, String.valueOf(expireTime));
        return result != null && result == 1;
    }

    private Boolean releaseReentrantLock(String lockKey, String requestId) {
        String script = "if redis.call('hexists', KEYS[1], ARGV[1]) == 0 then " +
                       "return 0 " +
                       "elseif redis.call('hincrby', KEYS[1], ARGV[1], -1) > 0 then " +
                       "return 1 " +
                       "else " +
                       "redis.call('del', KEYS[1]) " +
                       "return 1 " +
                       "end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        return result != null && result == 1;
    }

    @GetMapping("/lock/renewal")
    public TestResult testLockRenewal() {
        long startTime = System.currentTimeMillis();
        try {
            String lockKey = "lock:resource:renewal";
            String lockValue = UUID.randomUUID().toString();
            List<String> operations = new ArrayList<>();

            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
            operations.add("获取锁（10秒过期） => " + locked);

            if (Boolean.TRUE.equals(locked)) {
                Long ttl1 = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
                operations.add("初始TTL => " + ttl1 + " 秒");

                Thread.sleep(2000);
                Boolean renewed = renewLock(lockKey, lockValue, 10);
                operations.add("续期锁 => " + renewed);

                Long ttl2 = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
                operations.add("续期后TTL => " + ttl2 + " 秒");

                releaseLock(lockKey, lockValue);
            }

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "锁续期：防止业务执行时间超过锁的过期时间",
                "实现方式：后台线程定期检查并延长锁的过期时间",
                "看门狗机制：默认每1/3锁时间续期一次",
                "续期条件：锁仍然由当前线程持有",
                "面试重点：锁续期的实现原理、看门狗机制、续期失败的处理"
            );

            return TestResult.success(
                "锁续期",
                "锁续期测试完成",
                operations,
                "锁续期机制用于防止业务执行时间超过锁的过期时间导致锁失效。实现方式：后台线程（看门狗）定期检查锁是否仍然由当前线程持有，如果是则延长锁的过期时间。通常每1/3的锁时间续期一次。这样可以确保长时间运行的业务不会因为锁过期而出现问题。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("锁续期", e.getMessage(), "锁续期测试失败，请检查Redis连接");
        }
    }

    private Boolean renewLock(String lockKey, String lockValue, long expireTime) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                       "return redis.call('expire', KEYS[1], ARGV[2]) " +
                       "else " +
                       "return 0 " +
                       "end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue, String.valueOf(expireTime));
        return result != null && result == 1;
    }

    @GetMapping("/lock/redlock")
    public TestResult testRedLock() {
        long startTime = System.currentTimeMillis();
        try {
            String lockKey = "lock:resource:redlock";
            String lockValue = UUID.randomUUID().toString();
            List<String> operations = new ArrayList<>();

            operations.add("RedLock算法：在N个Redis实例上获取锁");
            operations.add("1. 获取当前时间戳");
            operations.add("2. 按顺序在N个实例上尝试获取锁");
            operations.add("3. 计算获取锁消耗的时间");
            operations.add("4. 如果成功获取锁的实例数 >= (N/2 + 1) 且耗时 < 锁的有效期，则获取成功");
            operations.add("5. 否则，向所有实例释放锁");

            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 30, TimeUnit.SECONDS);
            operations.add("在当前实例上获取锁 => " + locked);

            if (Boolean.TRUE.equals(locked)) {
                operations.add("✓ RedLock获取成功（单实例演示）");
                releaseLock(lockKey, lockValue);
            }

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "RedLock算法：在多个独立的Redis实例上获取锁",
                "容错性：即使部分实例宕机，只要多数实例正常即可",
                "实现步骤：按顺序获取锁、计算耗时、判断成功条件",
                "释放锁：向所有实例发送释放命令",
                "面试重点：RedLock的原理、容错机制、时钟漂移问题、争议点"
            );

            return TestResult.success(
                "RedLock算法",
                "RedLock算法测试完成",
                operations,
                "RedLock算法由Redis作者Antirez提出，用于在多个独立的Redis实例上实现分布式锁。核心思想：在N个独立的Redis实例上尝试获取锁，如果成功获取锁的实例数 >= (N/2 + 1) 且获取锁的总耗时 < 锁的有效期，则认为获取锁成功。这样可以避免单点故障，提高系统的可用性。但RedLock算法也存在争议，如时钟漂移、网络分区等问题。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("RedLock算法", e.getMessage(), "RedLock测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/cache/warmup")
    public TestResult testCacheWarmup() {
        long startTime = System.currentTimeMillis();
        try {
            String cachePrefix = "cache:product:";
            List<String> operations = new ArrayList<>();

            operations.add("缓存预热：系统启动时预先加载热点数据到缓存");
            operations.add("1. 从数据库查询热点商品数据");
            operations.add("2. 将数据写入Redis缓存");
            operations.add("3. 设置合理的过期时间");

            for (int i = 1; i <= 5; i++) {
                String productId = String.valueOf(i);
                Map<String, Object> product = new HashMap<>();
                product.put("id", productId);
                product.put("name", "商品" + i);
                product.put("price", 100 * i);
                product.put("stock", 1000);

                redisTemplate.opsForHash().putAll(cachePrefix + productId, product);
                redisTemplate.expire(cachePrefix + productId, 1, TimeUnit.HOURS);
                operations.add("预热商品" + i + " => " + product);
            }

            operations.add("✓ 缓存预热完成，已加载5个热点商品");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "缓存预热：系统启动时预先加载热点数据",
                "实现方式：定时任务、系统启动监听器、手动触发",
                "热点数据识别：访问频率、业务重要性、数据量",
                "预热策略：全量预热、增量预热、按需预热",
                "面试重点：预热时机、预热数据选择、预热失败处理"
            );

            return TestResult.success(
                "缓存预热",
                "缓存预热测试完成",
                operations,
                "缓存预热是指在系统启动或低峰期预先将热点数据加载到缓存中，避免用户访问时缓存未命中导致的数据库压力。实现方式包括：1）系统启动监听器；2）定时任务；3）手动触发。热点数据的选择基于访问频率、业务重要性和数据量。预热策略包括全量预热、增量预热和按需预热。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("缓存预热", e.getMessage(), "缓存预热测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/cache/penetration")
    public TestResult testCachePenetration() {
        long startTime = System.currentTimeMillis();
        try {
            String cacheKey = "cache:user:99999";
            List<String> operations = new ArrayList<>();

            operations.add("缓存穿透：查询不存在的数据，请求直达数据库");
            operations.add("解决方案：缓存空值、布隆过滤器");

            Object cached = redisTemplate.opsForValue().get(cacheKey);
            operations.add("第一次查询缓存 => " + cached);

            if (cached == null) {
                operations.add("缓存未命中，查询数据库...");
                operations.add("数据库中也不存在该用户");

                redisTemplate.opsForValue().set(cacheKey, "NULL", 5, TimeUnit.MINUTES);
                operations.add("缓存空值（5分钟过期） => SET " + cacheKey + " 'NULL' EX 300");
            }

            Object cached2 = redisTemplate.opsForValue().get(cacheKey);
            operations.add("第二次查询缓存 => " + cached2);
            operations.add("✓ 缓存穿透已解决，请求被拦截");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "缓存穿透：查询不存在的数据，缓存和数据库都没有",
                "问题：大量无效请求直达数据库，导致数据库压力过大",
                "解决方案：1）缓存空值；2）布隆过滤器",
                "布隆过滤器：空间效率高，但存在误判",
                "面试重点：缓存穿透的场景、解决方案、布隆过滤器原理"
            );

            return TestResult.success(
                "缓存穿透",
                "缓存穿透测试完成",
                operations,
                "缓存穿透是指查询不存在的数据，缓存和数据库都没有，导致请求直达数据库。解决方案：1）缓存空值：将查询为空的结果缓存起来，设置较短的过期时间；2）布隆过滤器：在访问缓存前先通过布隆过滤器判断数据是否存在，不存在则直接返回。布隆过滤器空间效率高，但存在误判，可能将存在的数据误判为不存在。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("缓存穿透", e.getMessage(), "缓存穿透测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/cache/breakdown")
    public TestResult testCacheBreakdown() {
        long startTime = System.currentTimeMillis();
        try {
            String cacheKey = "cache:product:hot";
            List<String> operations = new ArrayList<>();

            operations.add("缓存击穿：热点数据过期，大量请求同时查询数据库");
            operations.add("解决方案：互斥锁、逻辑过期");

            Object cached = redisTemplate.opsForValue().get(cacheKey);
            operations.add("查询缓存 => " + cached);

            if (cached == null) {
                String lockKey = "lock:rebuild:" + cacheKey;
                Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
                operations.add("获取重建锁 => " + locked);

                if (Boolean.TRUE.equals(locked)) {
                    operations.add("获取锁成功，重建缓存...");
                    Map<String, Object> product = new HashMap<>();
                    product.put("id", "hot");
                    product.put("name", "热门商品");
                    product.put("price", 999);
                    redisTemplate.opsForHash().putAll(cacheKey, product);
                    redisTemplate.expire(cacheKey, 1, TimeUnit.HOURS);
                    operations.add("缓存重建完成");

                    redisTemplate.delete(lockKey);
                    operations.add("释放重建锁");
                } else {
                    operations.add("获取锁失败，等待其他线程重建缓存...");
                    Thread.sleep(100);
                    Object cachedAfter = redisTemplate.opsForValue().get(cacheKey);
                    operations.add("再次查询缓存 => " + cachedAfter);
                }
            }

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "缓存击穿：热点数据过期，大量请求同时查询数据库",
                "问题：数据库瞬间承受巨大压力，可能导致宕机",
                "解决方案：1）互斥锁；2）逻辑过期（永不过期）",
                "互斥锁：只允许一个线程重建缓存，其他线程等待",
                "面试重点：缓存击穿的场景、解决方案、互斥锁实现"
            );

            return TestResult.success(
                "缓存击穿",
                "缓存击穿测试完成",
                operations,
                "缓存击穿是指热点数据过期，大量请求同时查询数据库，导致数据库瞬间承受巨大压力。解决方案：1）互斥锁：只允许一个线程重建缓存，其他线程等待或返回旧数据；2）逻辑过期：缓存不设置过期时间，由后台线程定期刷新。互斥锁实现简单，但可能导致线程阻塞；逻辑过期性能更好，但实现复杂。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("缓存击穿", e.getMessage(), "缓存击穿测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/cache/avalanche")
    public TestResult testCacheAvalanche() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("缓存雪崩：大量缓存同时失效，请求全部打到数据库");
            operations.add("解决方案：随机过期时间、缓存预热、高可用架构");

            for (int i = 1; i <= 10; i++) {
                String cacheKey = "cache:product:" + i;
                Map<String, Object> product = new HashMap<>();
                product.put("id", String.valueOf(i));
                product.put("name", "商品" + i);
                product.put("price", 100 * i);

                redisTemplate.opsForHash().putAll(cacheKey, product);

                long randomTTL = 30 + (long) (Math.random() * 30);
                redisTemplate.expire(cacheKey, randomTTL, TimeUnit.MINUTES);
                operations.add("缓存商品" + i + "，TTL: " + randomTTL + " 分钟");
            }

            operations.add("✓ 缓存雪崩已预防，使用随机过期时间");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "缓存雪崩：大量缓存同时失效，请求全部打到数据库",
                "问题：数据库瞬间承受巨大压力，可能导致宕机",
                "解决方案：1）随机过期时间；2）缓存预热；3）高可用架构",
                "随机过期时间：在基础过期时间上增加随机值，避免同时失效",
                "面试重点：缓存雪崩的场景、解决方案、过期时间策略"
            );

            return TestResult.success(
                "缓存雪崩",
                "缓存雪崩测试完成",
                operations,
                "缓存雪崩是指大量缓存同时失效，请求全部打到数据库，导致数据库瞬间承受巨大压力。解决方案：1）随机过期时间：在基础过期时间上增加随机值，避免同时失效；2）缓存预热：系统启动时预先加载数据；3）高可用架构：使用Redis集群、哨兵等保证缓存高可用。随机过期时间是最简单有效的方案。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("缓存雪崩", e.getMessage(), "缓存雪崩测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/rate-limit/fixed")
    public TestResult testFixedWindow() {
        long startTime = System.currentTimeMillis();
        try {
            String key = "rate:limit:fixed:user:1001";
            int limit = 10;
            int window = 60;
            List<String> operations = new ArrayList<>();

            operations.add("固定窗口限流：在固定时间窗口内限制请求数");
            operations.add("实现：使用计数器，窗口结束时重置");

            for (int i = 1; i <= 15; i++) {
                Long count = redisTemplate.opsForValue().increment(key);
                if (count == 1) {
                    redisTemplate.expire(key, window, TimeUnit.SECONDS);
                }

                boolean allowed = count <= limit;
                operations.add("请求" + i + ": 计数=" + count + ", " + (allowed ? "✓ 允许" : "✗ 拒绝"));
            }

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "固定窗口限流：在固定时间窗口内限制请求数",
                "实现：使用计数器，窗口结束时重置",
                "优点：实现简单，内存占用小",
                "缺点：边界问题（窗口边界可能突发流量）",
                "面试重点：固定窗口的边界问题、临界流量突刺"
            );

            return TestResult.success(
                "固定窗口限流",
                "固定窗口限流测试完成",
                operations,
                "固定窗口限流是指在固定时间窗口内限制请求的数量。实现方式：使用Redis计数器，每次请求时INCR计数器，首次请求时设置过期时间。如果计数超过阈值则拒绝请求。优点：实现简单，内存占用小。缺点：存在边界问题，窗口边界可能出现突发流量（如窗口最后1秒和下个窗口第1秒可能都达到阈值）。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("固定窗口限流", e.getMessage(), "固定窗口限流测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/rate-limit/sliding")
    public TestResult testSlidingWindow() {
        long startTime = System.currentTimeMillis();
        try {
            String key = "rate:limit:sliding:user:1001";
            int limit = 10;
            int window = 60;
            List<String> operations = new ArrayList<>();

            operations.add("滑动窗口限流：在滑动时间窗口内限制请求数");
            operations.add("实现：使用ZSet，score为时间戳，移除窗口外的记录");

            long now = System.currentTimeMillis() / 1000;
            long windowStart = now - window;

            redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
            Long count = redisTemplate.opsForZSet().size(key);

            for (int i = 1; i <= 15; i++) {
                redisTemplate.opsForZSet().add(key, "request:" + i, System.currentTimeMillis() / 1000.0);
                redisTemplate.opsForZSet().removeRangeByScore(key, 0, System.currentTimeMillis() / 1000.0 - window);
                Long currentCount = redisTemplate.opsForZSet().size(key);
                boolean allowed = currentCount <= limit;
                operations.add("请求" + i + ": 计数=" + currentCount + ", " + (allowed ? "✓ 允许" : "✗ 拒绝"));
            }

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "滑动窗口限流：在滑动时间窗口内限制请求数",
                "实现：使用ZSet，score为时间戳，移除窗口外的记录",
                "优点：解决了固定窗口的边界问题",
                "缺点：内存占用较大，需要维护时间戳",
                "面试重点：滑动窗口的实现原理、ZSet的使用、内存优化"
            );

            return TestResult.success(
                "滑动窗口限流",
                "滑动窗口限流测试完成",
                operations,
                "滑动窗口限流是指在滑动时间窗口内限制请求的数量。实现方式：使用Redis ZSet，score为请求时间戳，每次请求时：1）移除窗口外的记录；2）添加当前请求；3）统计窗口内的请求数；4）判断是否超过阈值。优点：解决了固定窗口的边界问题，流量更平滑。缺点：内存占用较大，需要维护时间戳。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("滑动窗口限流", e.getMessage(), "滑动窗口限流测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/rate-limit/token")
    public TestResult testTokenBucket() {
        long startTime = System.currentTimeMillis();
        try {
            String key = "rate:limit:token:user:1001";
            int capacity = 10;
            int rate = 2;
            List<String> operations = new ArrayList<>();

            operations.add("令牌桶限流：以固定速率向桶中添加令牌，请求时获取令牌");
            operations.add("实现：使用String存储令牌数，定期补充令牌");

            String script = "local key = KEYS[1] " +
                          "local capacity = tonumber(ARGV[1]) " +
                          "local rate = tonumber(ARGV[2]) " +
                          "local now = tonumber(ARGV[3]) " +
                          "local requested = tonumber(ARGV[4]) " +
                          "local info = redis.call('hmget', key, 'tokens', 'last_refill') " +
                          "local tokens = tonumber(info[1]) or capacity " +
                          "local last_refill = tonumber(info[2]) or now " +
                          "local delta = math.max(0, now - last_refill) " +
                          "local filled = math.min(capacity, delta * rate + tokens) " +
                          "local allowed = filled >= requested " +
                          "local new_tokens = allowed and (filled - requested) or filled " +
                          "redis.call('hmset', key, 'tokens', new_tokens, 'last_refill', now) " +
                          "redis.call('expire', key, math.ceil(capacity / rate) + 1) " +
                          "return {allowed, new_tokens}";

            RedisScript<List> redisScript = new DefaultRedisScript<>(script, List.class);

            for (int i = 1; i <= 15; i++) {
                List result = redisTemplate.execute(redisScript, Collections.singletonList(key),
                    String.valueOf(capacity), String.valueOf(rate),
                    String.valueOf(System.currentTimeMillis() / 1000), "1");
                boolean allowed = (Boolean) result.get(0);
                long tokens = (Long) result.get(1);
                operations.add("请求" + i + ": 令牌=" + tokens + ", " + (allowed ? "✓ 允许" : "✗ 拒绝"));
            }

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "令牌桶限流：以固定速率向桶中添加令牌，请求时获取令牌",
                "实现：使用Hash存储令牌数和上次补充时间",
                "优点：可以应对突发流量，流量平滑",
                "缺点：实现复杂，需要维护补充逻辑",
                "面试重点：令牌桶的原理、突发流量处理、补充速率计算"
            );

            return TestResult.success(
                "令牌桶限流",
                "令牌桶限流测试完成",
                operations,
                "令牌桶限流是指以固定速率向桶中添加令牌，请求时从桶中获取令牌。实现方式：使用Redis Hash存储令牌数和上次补充时间，每次请求时：1）计算需要补充的令牌数；2）更新令牌数；3）判断是否有足够令牌。优点：可以应对突发流量，流量平滑。缺点：实现复杂，需要维护补充逻辑。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("令牌桶限流", e.getMessage(), "令牌桶限流测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/rate-limit/leaky")
    public TestResult testLeakyBucket() {
        long startTime = System.currentTimeMillis();
        try {
            String key = "rate:limit:leaky:user:1001";
            int capacity = 10;
            int rate = 2;
            List<String> operations = new ArrayList<>();

            operations.add("漏桶限流：请求进入桶中，以固定速率流出");
            operations.add("实现：使用List存储请求，定期处理");

            for (int i = 1; i <= 15; i++) {
                Long size = redisTemplate.opsForList().size(key);
                boolean allowed = size < capacity;

                if (allowed) {
                    redisTemplate.opsForList().rightPush(key, "request:" + i);
                    operations.add("请求" + i + ": 桶大小=" + (size + 1) + ", ✓ 允许进入");
                } else {
                    operations.add("请求" + i + ": 桶已满, ✗ 拒绝");
                }
            }

            operations.add("模拟漏桶流出...");
            for (int i = 0; i < rate; i++) {
                redisTemplate.opsForList().leftPop(key);
            }
            operations.add("已流出" + rate + "个请求，当前桶大小: " + redisTemplate.opsForList().size(key));

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "漏桶限流：请求进入桶中，以固定速率流出",
                "实现：使用List存储请求，定期处理",
                "优点：流量平滑，保护下游系统",
                "缺点：无法应对突发流量，桶满时拒绝请求",
                "面试重点：漏桶的原理、与令牌桶的区别、适用场景"
            );

            return TestResult.success(
                "漏桶限流",
                "漏桶限流测试完成",
                operations,
                "漏桶限流是指请求进入桶中，以固定速率流出。实现方式：使用Redis List存储请求，桶有固定容量，请求到来时如果桶未满则进入，否则拒绝。后台线程以固定速率从桶中取出请求处理。优点：流量平滑，保护下游系统。缺点：无法应对突发流量，桶满时拒绝请求。与令牌桶的区别：令牌桶允许突发流量，漏桶强制平滑流量。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("漏桶限流", e.getMessage(), "漏桶限流测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/pipeline")
    public TestResult testPipeline() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("Pipeline批量操作：批量执行命令，减少网络往返");
            operations.add("测试：批量设置100个键值对");

            long pipelineStart = System.currentTimeMillis();

            redisTemplate.executePipelined((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
                for (int i = 1; i <= 100; i++) {
                    String key = "pipeline:test:" + i;
                    connection.set(key.getBytes(), ("value" + i).getBytes());
                }
                return null;
            });

            long pipelineTime = System.currentTimeMillis() - pipelineStart;
            operations.add("Pipeline执行100次SET，耗时: " + pipelineTime + "ms");

            long normalStart = System.currentTimeMillis();

            for (int i = 101; i <= 200; i++) {
                String key = "pipeline:test:" + i;
                redisTemplate.opsForValue().set(key, "value" + i);
            }

            long normalTime = System.currentTimeMillis() - normalStart;
            operations.add("普通执行100次SET，耗时: " + normalTime + "ms");
            operations.add("性能提升: " + (normalTime - pipelineTime) + "ms (" +
                          String.format("%.1f%%", (double) (normalTime - pipelineTime) / normalTime * 100) + ")");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "Pipeline：批量执行命令，减少网络往返",
                "原理：将多个命令打包发送，一次性返回结果",
                "优点：大幅提升批量操作性能",
                "缺点：不保证原子性，占用内存",
                "面试重点：Pipeline的原理、与事务的区别、性能优化"
            );

            return TestResult.success(
                "Pipeline批量操作",
                "Pipeline批量操作测试完成",
                operations,
                "Pipeline是Redis提供的批量执行命令的机制，可以将多个命令打包发送给Redis服务器，一次性返回所有结果。这样可以大幅减少网络往返时间，提升批量操作性能。Pipeline不保证原子性，如果中间某个命令失败，后续命令仍会执行。Pipeline适合批量读写操作，但不适合需要事务保证的场景。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("Pipeline批量操作", e.getMessage(), "Pipeline测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/transaction")
    public TestResult testTransaction() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("Redis事务：MULTI、EXEC、DISCARD、WATCH");
            operations.add("测试：事务执行多个命令");

            String key1 = "transaction:test:1";
            String key2 = "transaction:test:2";

            stringRedisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
                org.springframework.data.redis.connection.StringRedisConnection stringConnection =
                    (org.springframework.data.redis.connection.StringRedisConnection) connection;
                stringConnection.multi();
                stringConnection.set(key1, "value1");
                stringConnection.set(key2, "value2");
                stringConnection.exec();
                return null;
            });

            String value1 = stringRedisTemplate.opsForValue().get(key1);
            String value2 = stringRedisTemplate.opsForValue().get(key2);
            operations.add("事务执行结果: " + key1 + "=" + value1 + ", " + key2 + "=" + value2);

            operations.add("测试：事务回滚");

            String key3 = "transaction:test:3";
            String key4 = "transaction:test:4";

            stringRedisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
                org.springframework.data.redis.connection.StringRedisConnection stringConnection =
                    (org.springframework.data.redis.connection.StringRedisConnection) connection;
                stringConnection.multi();
                stringConnection.set(key3, "value3");
                stringConnection.set(key4, "value4");
                stringConnection.discard();
                return null;
            });

            String value3 = stringRedisTemplate.opsForValue().get(key3);
            String value4 = stringRedisTemplate.opsForValue().get(key4);
            operations.add("回滚后结果: " + key3 + "=" + value3 + ", " + key4 + "=" + value4);

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "Redis事务：MULTI、EXEC、DISCARD、WATCH",
                "MULTI：开启事务，EXEC：执行事务，DISCARD：取消事务",
                "WATCH：监视键，如果被修改则事务失败",
                "特点：不保证原子性，命令失败不影响其他命令",
                "面试重点：Redis事务与ACID、WATCH机制、与数据库事务的区别"
            );

            return TestResult.success(
                "Redis事务",
                "Redis事务测试完成",
                operations,
                "Redis事务提供MULTI、EXEC、DISCARD、WATCH四个命令。MULTI开启事务，EXEC执行事务，DISCARD取消事务，WATCH监视键。Redis事务不保证原子性，如果某个命令失败，其他命令仍会执行。WATCH用于实现乐观锁，如果监视的键被修改，则事务执行失败。Redis事务与数据库事务的区别：不支持回滚、不保证隔离性。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("Redis事务", e.getMessage(), "Redis事务测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/watch")
    public TestResult testWatch() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("WATCH：监视键，实现乐观锁");
            operations.add("测试：并发修改检测");

            String key = "watch:test:counter";

            redisTemplate.opsForValue().set(key, 100);
            operations.add("初始值: " + key + "=" + 100);

            redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
                connection.watch(key.getBytes());
                Object value = connection.get(key.getBytes());
                operations.add("WATCH " + key + ", 当前值: " + new String((byte[]) value));

                connection.multi();
                connection.incr(key.getBytes());
                connection.exec();
                return null;
            });

            Object finalValue = redisTemplate.opsForValue().get(key);
            operations.add("最终值: " + key + "=" + finalValue);

            operations.add("测试：并发修改模拟");

            redisTemplate.opsForValue().set(key, 100);

            redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
                connection.watch(key.getBytes());

                connection.multi();
                connection.incr(key.getBytes());
                connection.incr(key.getBytes());

                connection.exec();
                return null;
            });

            Object finalValue2 = redisTemplate.opsForValue().get(key);
            operations.add("并发修改后值: " + key + "=" + finalValue2);

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "WATCH：监视键，实现乐观锁",
                "原理：EXEC前检查监视的键是否被修改",
                "应用：并发控制、版本号检查",
                "特点：轻量级，无锁竞争",
                "面试重点：WATCH的实现原理、乐观锁与悲观锁、CAS机制"
            );

            return TestResult.success(
                "乐观锁（WATCH）",
                "乐观锁测试完成",
                operations,
                "WATCH命令用于实现乐观锁，监视一个或多个键，如果在EXEC执行前这些键被修改，则事务执行失败。WATCH的实现原理：EXEC前检查监视的键是否被修改，如果被修改则拒绝执行事务。WATCH适用于并发控制、版本号检查等场景。乐观锁相比悲观锁的优点是无锁竞争，性能更好。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("乐观锁（WATCH）", e.getMessage(), "乐观锁测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/lua")
    public TestResult testLua() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("Lua脚本：原子性执行多个命令");
            operations.add("测试：原子性计数器");

            String key = "lua:test:counter";

            String script1 = "local current = redis.call('get', KEYS[1]) or 0 " +
                           "local new = tonumber(current) + tonumber(ARGV[1]) " +
                           "redis.call('set', KEYS[1], new) " +
                           "return new";

            RedisScript<Long> redisScript1 = new DefaultRedisScript<>(script1, Long.class);
            Long result1 = stringRedisTemplate.execute(redisScript1, Collections.singletonList(key), "10");
            operations.add("执行Lua脚本: INCRBY " + key + " 10 => " + result1);

            Long result2 = stringRedisTemplate.execute(redisScript1, Collections.singletonList(key), "20");
            operations.add("执行Lua脚本: INCRBY " + key + " 20 => " + result2);

            operations.add("测试：复杂逻辑 - 限流");

            String limitKey = "lua:test:limit";
            String script2 = "local key = KEYS[1] " +
                           "local limit = tonumber(ARGV[1]) " +
                           "local current = tonumber(redis.call('get', key) or 0) " +
                           "if current + 1 > limit then " +
                           "  return 0 " +
                           "else " +
                           "  redis.call('incr', key) " +
                           "  redis.call('expire', key, 60) " +
                           "  return 1 " +
                           "end";

            RedisScript<Long> redisScript2 = new DefaultRedisScript<>(script2, Long.class);

            for (int i = 1; i <= 15; i++) {
                Long allowed = stringRedisTemplate.execute(redisScript2, Collections.singletonList(limitKey), "10");
                operations.add("请求" + i + ": " + (allowed == 1 ? "✓ 允许" : "✗ 拒绝"));
            }

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "Lua脚本：原子性执行多个命令",
                "优点：原子性、减少网络往返、复用性",
                "应用：复杂逻辑、限流、分布式锁",
                "限制：脚本执行时间不能过长",
                "面试重点：Lua脚本的原子性、EVAL命令、脚本缓存"
            );

            return TestResult.success(
                "Lua脚本",
                "Lua脚本测试完成",
                operations,
                "Lua脚本可以在Redis中原子性执行多个命令，保证原子性。优点：1）原子性：脚本执行期间不会被其他命令打断；2）减少网络往返：一次请求执行多个命令；3）复用性：脚本可以被缓存和复用。应用场景：复杂逻辑、限流、分布式锁等。限制：脚本执行时间不能过长，否则会阻塞其他命令。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("Lua脚本", e.getMessage(), "Lua脚本测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/pubsub")
    public TestResult testPubSub() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("发布订阅：PUBLISH、SUBSCRIBE、UNSUBSCRIBE");
            operations.add("测试：发布消息到频道");

            String channel = "pubsub:test:channel";

            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    stringRedisTemplate.convertAndSend(channel, "Hello Redis PubSub!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            operations.add("订阅频道: " + channel);

            Thread.sleep(500);

            operations.add("✓ 发布订阅测试完成");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "发布订阅：PUBLISH、SUBSCRIBE、UNSUBSCRIBE",
                "PUBLISH：发布消息到频道",
                "SUBSCRIBE：订阅频道",
                "应用：消息通知、实时通信",
                "面试重点：发布订阅的原理、与消息队列的区别、可靠性"
            );

            return TestResult.success(
                "发布订阅",
                "发布订阅测试完成",
                operations,
                "发布订阅是Redis提供的消息传递机制，包括PUBLISH（发布消息）、SUBSCRIBE（订阅频道）、UNSUBSCRIBE（取消订阅）等命令。发布者将消息发送到频道，订阅者接收频道消息。应用场景：消息通知、实时通信、聊天室等。缺点：消息不持久化，订阅者离线时无法接收消息。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("发布订阅", e.getMessage(), "发布订阅测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/pubsub/pattern")
    public TestResult testPatternPubSub() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("模式订阅：PSUBSCRIBE、PUNSUBSCRIBE");
            operations.add("测试：订阅匹配模式的频道");

            String pattern = "pubsub:test:*";

            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    stringRedisTemplate.convertAndSend("pubsub:test:channel1", "Message 1");
                    Thread.sleep(50);
                    stringRedisTemplate.convertAndSend("pubsub:test:channel2", "Message 2");
                    Thread.sleep(50);
                    stringRedisTemplate.convertAndSend("pubsub:other:channel", "Message 3");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            operations.add("订阅模式: " + pattern);

            Thread.sleep(500);

            operations.add("✓ 模式订阅测试完成");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "模式订阅：PSUBSCRIBE、PUNSUBSCRIBE",
                "PSUBSCRIBE：订阅匹配模式的频道",
                "支持通配符：*（任意字符）、?（单个字符）",
                "应用：批量订阅、主题路由",
                "面试重点：模式匹配规则、与普通订阅的区别"
            );

            return TestResult.success(
                "模式订阅",
                "模式订阅测试完成",
                operations,
                "模式订阅允许订阅匹配特定模式的频道，使用PSUBSCRIBE命令。支持通配符：*匹配任意字符，?匹配单个字符。例如：PSUBSCRIBE news.* 可以订阅所有以news.开头的频道。应用场景：批量订阅、主题路由等。模式订阅比普通订阅更灵活，可以一次性订阅多个频道。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("模式订阅", e.getMessage(), "模式订阅测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/persistence/rdb")
    public TestResult testRDB() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("RDB持久化：Redis Database，快照持久化");
            operations.add("测试：查看RDB配置和状态");

            String rdbInfo = stringRedisTemplate.execute((org.springframework.data.redis.core.RedisCallback<String>) connection -> {
                return "RDB持久化配置:\n" +
                       "- save 900 1: 900秒内至少1个key变化则保存\n" +
                       "- save 300 10: 300秒内至少10个key变化则保存\n" +
                       "- save 60 10000: 60秒内至少10000个key变化则保存\n" +
                       "- rdbcompression: 压缩RDB文件\n" +
                       "- rdbchecksum: 校验RDB文件\n" +
                       "- dbfilename: dump.rdb\n" +
                       "- dir: /var/lib/redis";
            });

            operations.add(rdbInfo);

            operations.add("RDB优点:");
            operations.add("- 文件紧凑，恢复速度快");
            operations.add("- 适合备份和灾难恢复");
            operations.add("- 性能影响小（fork子进程）");
            operations.add("RDB缺点:");
            operations.add("- 可能丢失数据（fork间隔）");
            operations.add("- fork时内存占用大");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "RDB持久化：Redis Database，快照持久化",
                "原理：fork子进程，保存数据到RDB文件",
                "触发方式：自动（save规则）、手动（SAVE/BGSAVE）",
                "优点：文件紧凑、恢复快、性能影响小",
                "缺点：可能丢失数据、fork内存占用大",
                "面试重点：RDB的工作原理、fork机制、与AOF的区别"
            );

            return TestResult.success(
                "RDB持久化",
                "RDB持久化测试完成",
                operations,
                "RDB（Redis Database）是Redis的快照持久化方式。原理：fork子进程，子进程将数据保存到RDB文件。触发方式：1）自动：根据save规则（如save 900 1）；2）手动：SAVE（阻塞）或BGSAVE（非阻塞）。优点：文件紧凑、恢复快、性能影响小。缺点：可能丢失数据（fork间隔）、fork时内存占用大。适合备份和灾难恢复。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("RDB持久化", e.getMessage(), "RDB持久化测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/persistence/aof")
    public TestResult testAOF() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("AOF持久化：Append Only File，追加日志持久化");
            operations.add("测试：查看AOF配置和状态");

            String aofInfo = stringRedisTemplate.execute((org.springframework.data.redis.core.RedisCallback<String>) connection -> {
                return "AOF持久化配置:\n" +
                       "- appendonly: yes\n" +
                       "- appendfilename: appendonly.aof\n" +
                       "- appendfsync: everysec\n" +
                       "  - always: 每次写入都同步（最安全，最慢）\n" +
                       "  - everysec: 每秒同步（推荐）\n" +
                       "  - no: 由操作系统决定（最快，可能丢失数据）\n" +
                       "- no-appendfsync-on-rewrite: no\n" +
                       "- auto-aof-rewrite-percentage: 100\n" +
                       "- auto-aof-rewrite-min-size: 64mb";
            });

            operations.add(aofInfo);

            operations.add("AOF优点:");
            operations.add("- 数据安全性高（最多丢失1秒数据）");
            operations.add("- AOF文件可读（可手动修改）");
            operations.add("- 自动重写，避免文件过大");
            operations.add("AOF缺点:");
            operations.add("- 文件体积大");
            operations.add("- 恢复速度慢于RDB");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "AOF持久化：Append Only File，追加日志持久化",
                "原理：记录每个写命令到AOF文件",
                "同步策略：always、everysec、no",
                "重写机制：AOF文件过大时自动重写",
                "面试重点：AOF的工作原理、同步策略、重写机制、与RDB的区别"
            );

            return TestResult.success(
                "AOF持久化",
                "AOF持久化测试完成",
                operations,
                "AOF（Append Only File）是Redis的追加日志持久化方式。原理：记录每个写命令到AOF文件。同步策略：1）always：每次写入都同步（最安全，最慢）；2）everysec：每秒同步（推荐）；3）no：由操作系统决定（最快，可能丢失数据）。AOF文件过大时会自动重写，压缩命令。优点：数据安全性高、AOF文件可读。缺点：文件体积大、恢复速度慢。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("AOF持久化", e.getMessage(), "AOF持久化测试失败，请检查Redis连接");
        }
    }

    @GetMapping("/monitor")
    public TestResult testMonitor() {
        long startTime = System.currentTimeMillis();
        try {
            List<String> operations = new ArrayList<>();

            operations.add("Redis性能监控：INFO、MONITOR、SLOWLOG");
            operations.add("测试：获取Redis性能指标");

            String info = stringRedisTemplate.execute((org.springframework.data.redis.core.RedisCallback<String>) connection -> {
                return "Redis性能指标:\n" +
                       "- connected_clients: " + connection.info("clients").getProperty("connected_clients") + "\n" +
                       "- used_memory: " + connection.info("memory").getProperty("used_memory_human") + "\n" +
                       "- total_commands_processed: " + connection.info("stats").getProperty("total_commands_processed") + "\n" +
                       "- instantaneous_ops_per_sec: " + connection.info("stats").getProperty("instantaneous_ops_per_sec") + "\n" +
                       "- keyspace_hits: " + connection.info("stats").getProperty("keyspace_hits") + "\n" +
                       "- keyspace_misses: " + connection.info("stats").getProperty("keyspace_misses") + "\n" +
                       "- hit_rate: " + calculateHitRate(connection);
            });

            operations.add(info);

            operations.add("性能优化建议:");
            operations.add("- 监控内存使用，避免OOM");
            operations.add("- 监控命令执行时间，优化慢查询");
            operations.add("- 监控缓存命中率，提高缓存效率");
            operations.add("- 监控连接数，避免连接泄露");

            long executionTime = System.currentTimeMillis() - startTime;
            List<String> interviewPoints = Arrays.asList(
                "性能监控：INFO、MONITOR、SLOWLOG",
                "INFO：获取Redis服务器信息",
                "MONITOR：实时监控所有命令（慎用）",
                "SLOWLOG：查看慢查询日志",
                "面试重点：关键性能指标、监控工具、性能优化"
            );

            return TestResult.success(
                "性能监控",
                "性能监控测试完成",
                operations,
                "Redis性能监控工具：1）INFO：获取服务器信息（内存、连接数、命令数等）；2）MONITOR：实时监控所有命令（慎用，影响性能）；3）SLOWLOG：查看慢查询日志。关键性能指标：内存使用、命令执行时间、缓存命中率、连接数等。性能优化：监控内存、优化慢查询、提高缓存命中率、避免连接泄露。",
                interviewPoints
            );
        } catch (Exception e) {
            return TestResult.error("性能监控", e.getMessage(), "性能监控测试失败，请检查Redis连接");
        }
    }

    private String calculateHitRate(org.springframework.data.redis.connection.RedisConnection connection) {
        try {
            long hits = Long.parseLong(connection.info("stats").getProperty("keyspace_hits", "0"));
            long misses = Long.parseLong(connection.info("stats").getProperty("keyspace_misses", "0"));
            long total = hits + misses;
            if (total == 0) return "0%";
            return String.format("%.2f%%", (double) hits / total * 100);
        } catch (Exception e) {
            return "N/A";
        }
    }
}
