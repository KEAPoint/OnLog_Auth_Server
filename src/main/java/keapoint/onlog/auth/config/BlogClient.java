package keapoint.onlog.auth.config;

import keapoint.onlog.auth.base.BaseResponse;
import keapoint.onlog.auth.dto.blog.BlogDto;
import keapoint.onlog.auth.dto.blog.BlogProfileDto;
import keapoint.onlog.auth.dto.blog.PostCreateBlogReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(value = "blogClient", url = "172.16.213.23:8080/blog")
public interface BlogClient {

    @GetMapping("")
    BaseResponse<BlogProfileDto> getMyProfile(@RequestParam("blog_id") UUID blogId);

    @PostMapping("")
    BaseResponse<BlogDto> createBlog(@RequestBody PostCreateBlogReqDto data);
}
