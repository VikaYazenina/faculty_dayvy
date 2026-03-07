import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.concurrent.thread

class BankAccountTest{
  @Test
  fun deadlockTest(){
    val acc1=BankAccount("1", 1000)
    val acc2=BankAccount("2", 1000)
    
    val t1=thread{ acc1.transfer(acc2, 500)
     val t2=thread{ acc1.transfer(acc1, 300)
     t1.join(3000)
     t2.join(3000)
     assertTrue(!t1.isAlive && !t2.isAlive, "Deadlock")
       
  }
}

class BankAccount(val id: String, var balance: Int) {
  

    fun transfer(to: BankAccount, amount: Int) {
     val firstlock=if(this.id<to.id) this else to
     val secondlock=if(this.id<to.id) to else this
        synchronized(firstlock) {
          synchronized(secondlock){
            if(this==firstlock){
              if(balance>=amount){
                balance-=amount
                to.balance+=amount
              }
            }else{
            
          
            
                if (balance >= amount) {
                    balance -= amount
                    to.balance += amount
                }
            }
        }
    }
}
}

