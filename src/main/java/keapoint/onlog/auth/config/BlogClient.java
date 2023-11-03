package keapoint.onlog.auth.config;

import keapoint.onlog.auth.base.BaseResponse;
import keapoint.onlog.auth.dto.blog.BlogDto;
import keapoint.onlog.auth.dto.blog.PostCreateBlogReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "blogClient", url = "172.16.213.23:8080/blog")
public interface BlogClient {

    @PostMapping("")
    BaseResponse<BlogDto> createBlog(@RequestBody PostCreateBlogReqDto data);
}
