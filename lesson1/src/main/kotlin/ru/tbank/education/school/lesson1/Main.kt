package ru.tbank.education.school.lesson1
fun calculate(a: Double, b: Double, operation: OperationType): Double? {
    when(operation){
        OperationType.ADD-> a+b
        OperationType.SUBTRACT  -> a-b
        OperationType.MULTIPLY -> a*b
        OperationType.DIVIDE -> if (b.toInt()!=0) a/b else null
        else -> a+b
    }.toString()
    return
}

fun main() {
    print("Hello world")
    println("Hello world")
    val c=3
    val d="hello $c people"
    println(d)
    var a=2
    println(a+1)

}
