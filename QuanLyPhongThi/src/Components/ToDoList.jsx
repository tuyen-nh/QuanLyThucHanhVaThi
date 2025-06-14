import { useContext, useEffect, useState } from "react"
import { ToDoContext } from "../store/context"
import { ToDoProvider } from "../store/Provide"

export default function ToDoList() {

    const toDoStore = useContext(ToDoContext)
    // console.log(toDoStore)
    const { state, dispath } = toDoStore
    // console.log(state)



    // State để lưu trữ dữ liệu từ API
    const [apiData, setApiData] = useState([]);

    useEffect(() => {
        // Gọi API khi component được render lần đầu (tương đương componentDidMount trong class component)
        fetch('http://localhost:8080/todos')
            .then(response => response.json())
            .then(data => {
                // Lưu trữ dữ liệu từ API vào state
                setApiData(data);
                console.log(data)
            })
            .catch(error => console.error('Error fetching data:', error));
    }, []); // Tham số thứ hai là một mảng rỗng để chỉ gọi API một lần khi component mount



    return <div>

        <input type="text"
            onChange={(e) => dispath({
                type: "On_Change",
                payload: e.target.value,
            })}
            value={state.toDoInput}
            
        />
        <button
            onClick={() => dispath({
                type: "ADD"
            })}
            className="direct"
        >{
              state.mark?"Update":"Add"  
            }
        </button>
        <div>
            <ul>
                {
                    state.todos.map((todo, index) => {
                        return <li key={index} style={{ display: 'flex', alignItems: 'center' }}>
                            {todo}
                            <p style={{ marginLeft: "40px", cursor: "pointer" }}
                                onClick={() => dispath({
                                    type: "Delete",
                                    id: index,
                                })}

                            >X</p>
                            <p style={{
                                marginLeft: "40px",
                                cursor: "pointer",
                            }}
                                onClick={() => dispath({
                                    type: "Update",
                                    id: index,
                                    // mark: true,
                                })}

                            >
                                Update
                            </p>
                        </li>
                    }
                    )
                }
            </ul>

        </div>
    </div>
}