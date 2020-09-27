package com.offcn.sellergoods.service;

import com.offcn.pojo.TbSeller;
import com.offcn.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public SellerService getSellerService() {
        return sellerService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 认证
        ArrayList<GrantedAuthority> grantedList = new ArrayList<GrantedAuthority>();
        grantedList.add(new SimpleGrantedAuthority("ROLE_USER"));
        // username 就是sellerId，可以通过sellerId获取seller所有信息（其中包括密码）
        TbSeller user = sellerService.findOne(username);
        if(user.getStatus().equals("1")) {
            // 认证加到username中
            return new User(username, user.getPassword(), grantedList);
        }else{
            return null;
        }

    }
}
