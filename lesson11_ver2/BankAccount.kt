package homework

class BankAccount(val id: String, var balance: Int) {

    fun transfer(to: BankAccount, amount: Int) {
        
        if (this.id == to.id) return

        val firstLock = if (this.id < to.id) this else to
        val secondLock = if (this.id < to.id) to else this

        synchronized(firstLock) {
            Thread.sleep(10) 
            synchronized(secondLock) {
                if (balance >= amount) {
                    balance -= amount
                    to.balance += amount
                }
            }
        }
    }
}
