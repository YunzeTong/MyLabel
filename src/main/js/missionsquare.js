import { Button, Menu, Card, Layout, Row, Col, Modal, Form, Image, Divider } from "antd";
import React, { Component } from "react";
import 'antd/dist/antd.css';
import ParticlesBg from "particles-bg";
import axios from "axios";
const { Header, Content, Footer } = Layout;
import cookie from 'react-cookies'


class MissionSquare extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            cur_user_id: cookie.load("cur_user_id"),
            mission_list: []
        }
        console.log("currrent_user_id: " + this.state.cur_user_id)
        this.all_mission_get()
    }

    go_mypic = () =>{
        window.location.href = '/mypic'
    }
    go_mytask = () =>{
        window.location.href = '/mytask'
    }
    exitlogin(){
        //清除当前账号
        cookie.remove('cur_user_id')
        window.location.href = '/login'
    }

    all_mission_get = () =>{
        console.log("当前帐号:"+this.state.cur_user_id)
        let id = this.state.cur_user_id
        axios.get(`/allmission_get/${id}`).then(
            response => {
                console.log(response)
                console.log(response.data)
                this.setState({
                    mission_list: response.data
                })
            }
        )
    }

    //认领任务，要分两步，新建task，修改mission状态，后者先不考虑
    claim_task = (mission_id, pub_acc, pic_url) =>{
        console.log(mission_id)
        console.log(pub_acc)
        console.log(pic_url)
        axios.post(`/claim_task`, {missionid: mission_id,
                                   claimeraccount: this.state.cur_user_id,
                                   publisheraccount: pub_acc,
                                   pictureurl: pic_url}).then(
            response => {
                console.log(response)
                console.log(response.data)
                if (response.data){
                    //认领成功
                    alert("您已成功认领")
                    window.location.href = '/missionsquare'
                }
                else{
                    alert("您已认领过该任务")
                    window.location.href = '/missionsquare'
                }
            }
        )
    }


    split_pic = (combine_url) => {
        let url_list = combine_url.split('$')
        // console.log(url_list)
        return url_list
        
    }

    




    render(){
        document.title = '任务广场';
        return (
            <>
            <Layout>
                <Header style={{ position: 'fixed', zIndex: 1, width: '100%' }}>
                    <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['3']}>
                        <Menu.Item key="1" onClick={this.go_mypic}>我的图片</Menu.Item>
                        <Menu.Item key="2" onClick={this.go_mytask}>和我相关的任务</Menu.Item>
                        <Menu.Item key="3">任务市场</Menu.Item>
                        <Menu.Item key="4" onClick={this.exitlogin.bind(this)}style={{color:"gold", background:"#FF3333"}}>退出登录</Menu.Item>
                    </Menu>
                 </Header>
                <Content className="site-layout" style={{ padding: '0 50px', marginTop: 64 }}>
                    <div className="site-layout-background" style={{ padding: 24, minHeight: 500 }}>
                    <Card title="所有未领取任务" style={{ width: 1390}}>
                    <div><br></br></div>
                    {this.state.mission_list.map((item, idx) => {
                        let url_list = this.split_pic(item['pictureurl'])
                        // console.log(url_list)
                        return (
                            <div>
                            <Divider orientation="left">mission {item['id']}:</Divider>
                            {url_list.map((iitem, idx) =>{
                                return (
                                <Row>
                                <Col span={6}>第{idx+1}张图</Col>
                                <Col span={10}>
                                <Image width={300} src={"https://res.cloudinary.com/yifengperson/image/upload/"+decodeURIComponent(iitem)} />
                                </Col>
                                <Col span={3}>发布者: {item['publisheraccount']}</Col>
                                </Row>
                            )})}
                            <Button shape="round" onClick={()=>this.claim_task(item['id'], item['publisheraccount'], item['pictureurl'])} type="primary" size="large" block>认领{item['id']}号任务</Button>
                            
                            </div>
                        )
                    })}
                    </Card>
                    
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center', fontFamily: 'cursive',fontSize:'large' }}>Presented By Tong Yuntze</Footer>
            </Layout>
            <ParticlesBg type="fountain" bg={true} />
            </>
        )
    }
}

export default MissionSquare;