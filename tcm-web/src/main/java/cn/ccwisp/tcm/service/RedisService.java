package cn.ccwisp.tcm.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mysql.cj.xdevapi.JsonArray;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

// TODO 帖子Id -> Set<点过赞的用户Id>
// TODO 帖子Id -> Set<收藏过的用户Id>
// TODO 帖子Id -> 赞总数
// TODO 帖子Id -> 收藏总数
// TODO 用户Id -> 验证码,自动过期时间
// TODO 用户Id -> 浏览记录
@Service
public class RedisService {
    private final String USER_LIKED_THREADS = "UserLikedThreads";
    private final String USER_FAV_THREADS = "UserFavThreads";
    private final String USER_HISTORY = "UserHistory";
    private final String USER_CAPTCHA = "UserCaptcha";
    private final String THREAD_LIKED_SUM = "ThreadLikedSum";
    private final String THREAD_FAV_SUM = "ThreadFavSum";
    private final String THREAD_VIEWS = "ThreadViews";
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    private HashSet<Integer> JSONArrayToSet(Object jsonArray) {
        JSONArray xx = (JSONArray)jsonArray;
        if (xx == null)
            return new HashSet<>();
        Integer[] ans = Arrays.copyOfRange(xx.toArray(), 0, xx.size(),Integer[].class);
        return new HashSet<>(Arrays.asList(ans));
    }
    private HashMap<String, Integer> JSONObjectToMap(Object jsonObject) {
        JSONObject xx = (JSONObject)jsonObject;
        if (xx == null)
            return new HashMap<>();
        return JSONObject.parseObject(xx.toJSONString(), new TypeReference<HashMap<String, Integer>>(){});
    }
    // 更新帖子的Like数
    private void SetLikeOfThread(int threadId, boolean increase) {
        if (redisTemplate.opsForHash().get(THREAD_LIKED_SUM, String.valueOf(threadId)) == null)
            redisTemplate.opsForHash().put(THREAD_LIKED_SUM, String.valueOf(threadId), 0);
        redisTemplate.opsForHash().increment(THREAD_LIKED_SUM, String.valueOf(threadId), increase?1:-1);
    }
    // 更新帖子的Fav数
    private void SetFavOfThread(int threadId, boolean increase) {
        if (redisTemplate.opsForHash().get(THREAD_FAV_SUM, String.valueOf(threadId)) == null)
            redisTemplate.opsForHash().put(THREAD_FAV_SUM, String.valueOf(threadId), 0);
        redisTemplate.opsForHash().increment(THREAD_FAV_SUM, String.valueOf(threadId), increase?1:-1);
    }
    // 获取帖子Like数
    public int GetLikeOfThread(int threadId) {
        Object sum = redisTemplate.opsForHash().get(THREAD_LIKED_SUM, String.valueOf(threadId));
        if (sum == null) return 0;
        return (int) sum;
    }
    // 获取帖子Fav数
    public int GetFavOfThread(int threadId) {
        Object sum = redisTemplate.opsForHash().get(THREAD_FAV_SUM, String.valueOf(threadId));
        if (sum == null) return 0;
        return (int) sum;
    }
    // 增加帖子Views数
    public void IncrementViewsCount(int threadId) {
        redisTemplate.opsForHash().increment(THREAD_VIEWS, String.valueOf(threadId), 1);
    }
    // 获取帖子Views数
    public int GetViewsOfThread(int threadId) {
        Object o = redisTemplate.opsForHash().get(THREAD_VIEWS, String.valueOf(threadId));
        if (o == null)
            o = 0;
        return (int) o;
    }
    // 用户Liked的所有帖子集合
    public HashSet<Integer> GetUserLikedThreadId(int userId) {
        return JSONArrayToSet(redisTemplate.opsForHash().get(USER_LIKED_THREADS, String.valueOf(userId)));
    }
    // 用户Fav的所有帖子集合
    public Set<Integer> GetUserFavThreadId(int userId) {
        return JSONArrayToSet(redisTemplate.opsForHash().get(USER_FAV_THREADS,  String.valueOf(userId)));
    }
    // 添加一个用户Like的帖子
    public boolean AddUserLikedThread(int userId, int threadId) {
        Set<Integer> liked = GetUserLikedThreadId(userId);
        if (liked.contains(threadId))
            return false;
        liked.add(threadId);
        redisTemplate.opsForHash().put(USER_LIKED_THREADS,  String.valueOf(userId), liked);
        SetLikeOfThread(threadId, true);
        return true;
    }
    // 取消一个用户Like的帖子
    public boolean RemoveUserLikedThread(int userId, int threadId) {
        Set<Integer> liked = GetUserLikedThreadId(userId);
        if (!liked.contains(threadId))
            return false;
        liked.remove(threadId);
        redisTemplate.opsForHash().put(USER_LIKED_THREADS,  String.valueOf(userId), liked);
        SetLikeOfThread(threadId, false);
        return true;
    }
    // 添加一个用户Fav的帖子
    public boolean AddUserFavThread(int userId, int threadId) {
        Set<Integer> fav = GetUserFavThreadId(userId);
        if (fav.contains(threadId))
            return false;
        fav.add(threadId);
        redisTemplate.opsForHash().put(USER_FAV_THREADS,  String.valueOf(userId), fav);
        SetFavOfThread(threadId, true);
        return true;
    }
    // 取消一个用户Fav的帖子
    public boolean RemoveUserFavThread(int userId, int threadId) {
        Set<Integer> fav = GetUserFavThreadId(userId);
        if (!fav.contains(threadId))
            return false;
        fav.remove(threadId);
        redisTemplate.opsForHash().put(USER_FAV_THREADS,  String.valueOf(userId), fav);
        SetFavOfThread(threadId, false);
        return true;
    }
    // 获取用户所有浏览记录
    public HashMap<String, Integer> getUserHistory(int userId) {
        return JSONObjectToMap(redisTemplate.opsForHash().get(USER_HISTORY, String.valueOf(userId)));
    }
    // 添加用户的浏览记录
    public void addUserHistory(int userId, int threadId) {
        HashMap<String, Integer> userHistory = getUserHistory(userId);
        userHistory.put(new DateTime().toString(), threadId);
        redisTemplate.opsForHash().put(USER_HISTORY,  String.valueOf(userId), userHistory);
    }
}
