
import React, { Component } from "react";
import ReactDOM from "react-dom";
import {BrowserRouter as Router, Route, Redirect, Switch} from 'react-router-dom';
import Login from "./login";
import Mypic from "./mypic";
import MissionSquare from "./missionsquare";
import Register from "./Register";
import Mytask from "./mytask";

export class App extends Component {

    render() {
        return (
        <Router>
            <Switch>
            <Route path="/" exact render={() => <Redirect to="/login" />}/>
            <Route path='/login' component={Login} />
            <Route path='/register' component={Register} />
            <Route path='/mypic' component={Mypic} />
            <Route path='/missionsquare' component={MissionSquare} />
            <Route path='/mytask' component={Mytask} />
            <Redirect from="/*" to="/login" />
            </Switch>
        </Router>
        );
    }
}

export default App;

ReactDOM.render(<App />, document.getElementById('root'));