import { useState } from 'react'
import SignIn from './Components/signIn'
import ToDoList from './Components/ToDoList'
// import reactLogo from './assets/react.svg'
// import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    
    <div>
       <SignIn/>
      {/* <AdminDashboard/> */}
       {/* <ToDoList/> */}
       {/* <AdminDashboard/> */}
      
      
    </div>
   
    
  )
}

export default App
