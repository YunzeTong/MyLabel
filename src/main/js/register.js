import { Button, Form, Input, Row} from "antd";
import React from "react";  //删了{Componenet}
import 'antd/dist/antd.css';
import ParticlesBg from "particles-bg";
import axios from "axios";
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons';

class Register extends React.Component{
    constructor(){
        super()
    }


    register = (values) =>{
        // console.log("name" + values.username)
        // console.log("accout" + values.account)
        // console.log("pwd" + values.password)
        // console.log("email" + values.email)
        axios.post(`/handle_register`, values).then(
            response => {
            console.log(response.data);
            if (response.data.id == null){
                alert("该账号已经被注册过，请重新注册")
                window.location.href = '/register'
            } else {
                alert("注册成功，即将返回登录界面")
                window.location.href = '/login'
            }
        }
        )
    }

    return(){
        console.log('返回登录')
        window.location.href = '/'
    }

    render(){
        document.title = '注册账号';
        return (
            <>
            <div className="right">
            <Button danger type="primary" onClick={this.return.bind(this)}>返回登录</Button>
            <Row type="flex" justify="center" align="middle" style={{minHeight:'100vh'}}>
            <Form  onFinish={this.register.bind(this)}  labelCol={{span: 10,}} wrapperCol={{span: 12,}} initialValues={{remember: true,}} autoComplete="off">
            <Form.Item label="姓名" name="username" rules={[{required: true,message: '请输入您的姓名!'},]}>
                <Input size="large" placeholder="用户名"/>
            </Form.Item>
            
            <Form.Item label="账号" name="account" rules={[{required: true,message: '请输入账号'},]}>
                <Input size="large" placeholder="账号"/>
            </Form.Item>

            <Form.Item label="密码" name="password" rules={[{required: true,message: '请输入密码!'},
                {
                    min: 6,
                    max: 12,
                    message: '密码长度应为6-12个字符',
                    trigger: 'blur'
                },
                {
                    pattern:/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$/,
                    message: '密码应包含数字和字母'
                }]}>
                <Input.Password size="large" placeholder="密码"/>
            </Form.Item>

            <Form.Item label="邮箱" name="email" rules={[
                                {
                                    required: true,
                                    message: '请输入您的邮箱地址',
                                    trigger: 'blur'
                                },
                                {
                                    pattern:/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/,
                                    message:'请输入正确的邮箱格式'
                                }
                            ]}>
                <Input size="large" placeholder="邮箱"/>
            </Form.Item>

            <Form.Item  wrapperCol={{offset: 10,}}>
                <Button type="primary" htmlType="submit">
                注册
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

export default Register;