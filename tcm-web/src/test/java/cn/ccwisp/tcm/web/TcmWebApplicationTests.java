package cn.ccwisp.tcm.web;

import cn.ccwisp.tcm.generated.domain.FmsComment;
import cn.ccwisp.tcm.generated.service.impl.FmsCommentServiceImpl;
import cn.ccwisp.tcm.search.service.TestService;
import cn.ccwisp.tcm.service.MailService;
import cn.ccwisp.tcm.service.RedisService;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class TcmWebApplicationTests {
	@Autowired
	TestService testService;
	@Autowired
	MailService mailService;
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Test
	void contextLoads() {
		System.out.println(new BCryptPasswordEncoder().encode("218817"));
	}

	@Test
	void SendMailTest() {
		mailService.send("749976734@qq.com", "欢迎", "你的验证马");
	}

	@Test
	void PinyinTest() {
		String s = PinyinHelper.toHanyuPinyinStringArray("白纸".charAt(0))[0];
		System.out.println(s.charAt(1));
	}

	@Test
	void RedisTest() {
		System.out.println(redisTemplate.getConnectionFactory().getConnection().ping());
		redisTemplate.opsForValue().set("222", "hello");
		System.out.println(redisTemplate.opsForValue().get("222"));

		redisTemplate.opsForHash().put("test", 10086, new HashSet<Integer>());

		HashSet<Integer> test = (HashSet<Integer>) redisTemplate.opsForHash().get("test", 10086);
		assert test != null;
		test.add(1234);
		redisTemplate.opsForHash().put("test", 10086, test);
		HashSet<Integer> test2 = (HashSet<Integer>) redisTemplate.opsForHash().get("test", 10086);
		for (Integer integer : test) {
			System.out.println(integer);
		}


		HashSet<Integer> test3 = (HashSet<Integer>) redisTemplate.opsForHash().get("test", 555555);
		System.out.println(test3);
	}

	@Test
	void SetTest() {
		HashSet<Integer> set = new HashSet<>();
		set.forEach(System.out::println);
		set.add(1);
		set.forEach(System.out::println);
		set.add(1);
		set.forEach(System.out::println);
		set.remove(1000);
		set.forEach(System.out::println);
	}

	@Test
	void TestIncrement() {
		redisTemplate.opsForValue().set("testnum", 3000);
		redisTemplate.opsForValue().increment("testnum");
		redisTemplate.opsForValue().increment("testnum");
		redisTemplate.opsForValue().increment("testnum");
		System.out.println(redisTemplate.opsForValue().get("testnum"));
	}
	@Autowired
	RedisService redisService;
	@Test
	void LikeTest() {
		System.out.println(redisService.GetLikeOfThread(2)); // 0
		System.out.println("用户like的所有帖子"); //
		Set<Integer> liked = redisService.GetUserLikedThreadId(1);
		for (Integer integer : liked) {
			System.out.println(integer);
		}
		System.out.println("contains?: " + liked.contains(2)); // false
		System.out.println(redisService.AddUserLikedThread(1, 2)); // true
		System.out.println(redisService.GetLikeOfThread(2)); // 1
	}

	@Test
	void SerializerTest() {
		if (redisTemplate.opsForHash().get("test", "10086") == null)
			redisTemplate.opsForHash().put("test", "10086", 0);
		redisTemplate.opsForHash().increment("test", "10086",1);
	}

	@Test
	void TestHashMapSerialize() {
		HashMap<DateTime, Integer> hashmap = (HashMap<DateTime, Integer>) redisTemplate.opsForHash().get("UserHistory", String.valueOf(10086));
	}

	@Autowired
	FmsCommentServiceImpl fmsCommentService;
	@Test
	void AddCommentTest() {
		FmsComment entity = new FmsComment();
		fmsCommentService.save(entity);
	}
}
