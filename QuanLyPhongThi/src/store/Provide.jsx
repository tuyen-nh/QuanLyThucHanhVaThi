import { useReducer } from "react"
import { ToDoContext } from "./context"
import { toDoReducer } from "./Reducer"
import { inotialState } from "./constant"

const ToDoProvider = ({children}) =>{
    //state là trạng thái được cập nhật 
    // dispath là dữ liệu được gửi về từ client và chuyền vào hàm toDoReducer 

    const [state,dispath] = useReducer(toDoReducer,inotialState)


    return <ToDoContext.Provider value={{
        state,
        dispath,
    }}>
        {children}
    </ToDoContext.Provider>
}
export {ToDoProvider}