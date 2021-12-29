import { Button, Form, Input, Row, Col } from "antd";
import React, { Component } from "react";
import 'antd/dist/antd.css';
import ParticlesBg from "particles-bg";
import axios from "axios";
import cookie from 'react-cookies'

class Login extends React.Component{
    
    log_in = async(values) =>{
        console.log("账号"+values.account)
        console.log("pwd"+values.password)
        console.log(values)
        axios.post(`/handle_log`, values).then(
            response => {
                // console.log(response);
                console.log(response.data)
                if (response.data){
                    //设置2小时后过期
                    let cookieTime = new Date(new Date().getTime + 2 * 3600 * 1000);
                    cookie.save("cur_user_id", values.account, {expires:cookieTime})
                    console.log(cookie.load("cur_user_id"))
                    window.location.href = '/mypic'
                }
                else{
                    alert("账号或密码错误，请重新登录")
                }
            }
        )

    }

    register(){
        console.log("前往注册账号")
        window.location.href = '/register'
    }


    render(){
        document.title = '登录';
        return (
            <>
            <div>
            <Button danger type="primary" onClick={this.register.bind(this)}>注册账号</Button>
            <Row type="flex" justify="center" align="middle" style={{minHeight:'100vh'}}>
            <Form labelCol={{span: 10,}} wrapperCol={{span: 12,}} onFinish={this.log_in.bind(this)} initialValues={{remember: true,}} autoComplete="off">
                <Form.Item label="Account" name="account" rules={[{required: true,message: 'Please input your account!'},]}>
                    <Input />
                </Form.Item>

            <Form.Item label="Password" name="password" rules={[{required: true,message: 'Please input your password!'},]}>
                <Input.Password />
            </Form.Item>

            <Form.Item  wrapperCol={{offset: 10,}}>
                <Button type="primary" htmlType="submit" shape="round">
                登录
                </Button>
            </Form.Item>
            </Form>
            </Row>
            </div>
            <ParticlesBg type="fountain" bg={true} />
            </>
        )
    }
}

export default Login;
