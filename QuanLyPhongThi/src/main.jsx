import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import { ToDoProvider } from './store/Provide'
import {createBrowserRouter, RouterProvider} from 'react-router-dom'
import DashBoard from './Components/DashBoard'
// import './index.css'

const routes = createBrowserRouter([
  {
    path: "/",
    element: <App/>
  },
  {
    path: "/DashBoard",
    element: <DashBoard/>
  }
])

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ToDoProvider>
      <RouterProvider router={routes}/>
    </ToDoProvider>
    
  </React.StrictMode>,
)
