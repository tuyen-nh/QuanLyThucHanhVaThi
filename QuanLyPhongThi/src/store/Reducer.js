const toDoReducer = (state,actions) =>{
    console.log(actions.type)
    console.log(state)
    switch(actions.type){
        case "On_Change":
            return{
                ...state,
                toDoInput:actions.payload,
            }
        case "ADD":
            return{
                ...state,
                todos:[...state.todos,state.toDoInput],
                toDoInput: "",
                mark: false,
            }
        case "Delete":
            // dấu "_" dùng để đại diện cho từng phần tử trong todos , nếu để trong ngoặc là 2 tham số thì 1 sẽ là giá trị, 2 sẽ là index
            return{
                ...state,
                todos: state.todos.filter((_,index) => index != actions.id),
            }
        case "Update":
            return{
                ...state,
                toDoInput: state.todos[actions.id],
                todos: state.todos.filter((_,index) => index != actions.id),
                mark:true,
                

            }
        
            
    }

}
export {toDoReducer}