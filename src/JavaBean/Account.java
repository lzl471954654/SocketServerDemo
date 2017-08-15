package JavaBean;

public class Account {
    private String account;
    private String password;

    public Account(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public Account(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        Account account1 = (Account) o;

        if (!getAccount().equals(account1.getAccount())) return false;
        return getPassword().equals(account1.getPassword());
    }

    @Override
    public int hashCode() {
        int result = getAccount().hashCode();
        result = 31 * result + getPassword().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
