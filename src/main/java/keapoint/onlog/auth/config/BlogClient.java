package keapoint.onlog.auth.config;

import keapoint.onlog.auth.base.BaseResponse;
import keapoint.onlog.auth.dto.blog.BlogDto;
import keapoint.onlog.auth.dto.blog.PostCreateBlogReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "blogClient", url = "ONLOG-BLOG-SERVER")
public interface BlogClient {

    @PostMapping("/blog")
    BaseResponse<BlogDto> createBlog(@RequestBody PostCreateBlogReqDto data);
}
