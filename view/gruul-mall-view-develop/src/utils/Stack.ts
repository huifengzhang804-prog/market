class Stack<T>{
    //栈的属性：数组
    items:Array<T> = [];

    push(element:T) {
        this.items.push(element);
    }
    //将元素压出栈
    pop():T {
        return this.items.pop();
    }
    //读取栈顶元素peek
    peek():T {
        return this.items.peek()
    }
    //读取是否为空栈isEmpty,返回布尔值
    isEmpty():boolean {
        return this.items.length == 0;
    }
    //获取元素个数size
    size():number {
        return this.items.length;
    }
}

export default Stack;