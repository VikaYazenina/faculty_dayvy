package ru.tbank.education.school.lesson1
enum class OperationType{
    ADD, SUBTRACT, MULTIPLY, DIVIDE
}
fun calculate(a: Double, b: Double, operation: OperationType): Double? {
    return when(operation){
        OperationType.ADD-> a+b
        OperationType.SUBTRACT  -> a-b
        OperationType.MULTIPLY -> a*b
        OperationType.DIVIDE -> if (b!=0.0) a/b else null
        else -> a+b
    }
}
fun main(){

}


