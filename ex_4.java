import java.util.*;
import com.chain.api.*;
import com.chain.http.*;
import com.chain.signing.*;

public class ex_4 {
    public static void main(String[] args) throws Exception {
        String Surfboard_company = "Surfboard_company";
        String SurfBoard = "SurfBoard";
        String newAccountName;
        String Number_of_New_Order=null;
        String duplicateaccountname=null;
        String duplicateaccountid=null;
        int quantity=0;
        boolean hasAccount = false;
        boolean isDuplicateCustomerAccount = false;
        boolean typeIncorret=true;
        int accountMoneyBalance=0;

        MockHsm.Key NewAccount_key = null;
        MockHsm.Key NewAccount_Dollar_key = null;
        MockHsm.Key NewAccount_Pledge_key = null;

        //Create a new client named client
        Client client = new Client();

        Account.Items accounts = new Account.QueryBuilder()
                .execute(client);

        //Duplicate account check
        while (accounts.hasNext()) {
            Account duplicate_account = accounts.next();

            //Check if there is any account named Surfboard_company already
            if (duplicate_account.alias.equals(Surfboard_company)) {
                hasAccount = true;
            }
        }

        //If there is no account named Surfboard_company, create the account and the according Key, Asset.
        if (!hasAccount) {

            MockHsm.Key Surfboard_company_key = MockHsm.Key.create(client);
            HsmSigner.addKey(Surfboard_company_key, MockHsm.getSignerClient(client));

            MockHsm.Key Surfboard__key = MockHsm.Key.create(client);
            HsmSigner.addKey(Surfboard__key, MockHsm.getSignerClient(client));

            new Asset.Builder()
                    .setAlias(SurfBoard)
                    .addRootXpub(Surfboard__key.xpub)
                    .setQuorum(1)
                    .addDefinitionField("issuer", Surfboard_company)
                    .create(client);


            new Account.Builder()
                    .setAlias(Surfboard_company)
                    .addRootXpub(Surfboard_company_key.xpub)
                    .setQuorum(1)
                    .create(client);

            //Initialize the inventory.
            Transaction.Template initial_Surfboard_Inventory = new Transaction.Builder()
                    .addAction(new Transaction.Action.Issue()
                            .setAssetAlias(SurfBoard)
                            .setAmount(1000)
                    ).addAction(new Transaction.Action.ControlWithAccount()
                            .setAccountAlias(Surfboard_company)
                            .setAssetAlias(SurfBoard)
                            .setAmount(1000)
                    ).build(client);

            Transaction.Template signedRetirementTransaction = HsmSigner.sign(initial_Surfboard_Inventory);

            Transaction.submit(client, signedRetirementTransaction);

        }


        Balance.Items inventoryBalances = new Balance.QueryBuilder()
                .setFilter("account_alias=$1")
                .addFilterParameter(Surfboard_company)
                .execute(client);

        //User input the name.
        System.out.println("Welcome to the online surfboard rental portal" + "\n" + "Please type in your name:");
        Scanner NewAccount = new Scanner(System.in);
        newAccountName = NewAccount.nextLine();

        while(newAccountName.equals(""))
        {System.out.println("Please type in correct name:");
            Scanner newAccount = new Scanner(System.in);
            newAccountName = newAccount.nextLine();
        }

        String NewAccount_Pledge = newAccountName + "_Pledge";
        String NewAccount_Dollar = newAccountName + "_Dollar";


        Account.Items oldaccountcheck = new Account.QueryBuilder()
                .execute(client);

        //Check whether there is any existing account for this name
        while (oldaccountcheck.hasNext()) {
            Account a = oldaccountcheck.next();
            if (a.alias.equals(newAccountName)) {
                duplicateaccountid = a.id;
                duplicateaccountname = a.alias;
                isDuplicateCustomerAccount=true;
            }
        }

        //If there is duplicate account, let the user input the number of surfboard.
        if (isDuplicateCustomerAccount) {

            System.out.println("You already have an account!" + "\n" + "Account ID:" + duplicateaccountid + ", Account Name:" + duplicateaccountname);
            Balance.Items balances = new Balance.QueryBuilder()
                    .setFilter("account_alias=$1 AND asset_alias=$2")
                    .addFilterParameter(newAccountName)
                    .addFilterParameter(NewAccount_Dollar)
                    .execute(client);

            while (balances.hasNext()) {
                Balance b = balances.next();
                accountMoneyBalance=(int)b.amount;
                System.out.println(
                        newAccountName+"'s balance of " + "Dollar" +
                                ": " + b.amount
                );
            }

            while (typeIncorret) {

                System.out.println("Welcome " + newAccountName + "\n" + "Please type in your number of ordering:");
                Scanner New_Order = new Scanner(System.in);
                Number_of_New_Order = New_Order.nextLine();
                typeIncorret=false;
                try {

                    quantity = Integer.parseInt(Number_of_New_Order);

                } catch (NumberFormatException e) {
                    System.out.println("You didn't type in a valid number!");
                    typeIncorret=true;
                }
            }

            while (quantity*15 > accountMoneyBalance) {
                System.out.println("Sorry, you don't have enough money, you only have "+ accountMoneyBalance + " dollar");
                System.out.println("You can only afford to rent " + accountMoneyBalance/15 + " surfboard.");
                System.out.println("Please retype your number of ordering:");
                Scanner New_Order = new Scanner(System.in);
                Number_of_New_Order = New_Order.nextLine();
                quantity = Integer.parseInt(Number_of_New_Order);
            }

            while (inventoryBalances.hasNext()) {
                Balance b = inventoryBalances.next();
                Boolean x = b.sumBy.get("asset_alias").equals("SurfBoard");
                if (x) {
                    while (quantity > b.amount || quantity<1 || quantity*15 > accountMoneyBalance) {
                        System.out.println("We currently have "+b.amount+" Surfboards in inventory, please rent 1 -- "+b.amount+" Surfboard");
                        Scanner New_Order = new Scanner(System.in);
                        Number_of_New_Order = New_Order.nextLine();
                        quantity = Integer.parseInt(Number_of_New_Order);
                    }
                }
            }

        } else {

            while (typeIncorret) {
                System.out.println("Welcome " + newAccountName + "\n" + "Your account name is: " + newAccountName + "\n" + "Please type in your number of ordering:");
                Scanner New_Order = new Scanner(System.in);
                Number_of_New_Order = New_Order.nextLine();
                typeIncorret = false;
                try {

                    quantity = Integer.parseInt(Number_of_New_Order);

                } catch (NumberFormatException e) {
                    System.out.println("You didn't type in a valid number!");
                    typeIncorret = true;
                }
            }

            int rn = 0;
            Random random = new Random();
            rn = random.nextInt(10000) + 10000;


            while (quantity*15 > rn) {
                System.out.println("Sorry, you don't have enough money, you only have "+ rn + " dollar");
                System.out.println("You can only afford to rent " + rn/15 + " surfboard.");
                System.out.println("Please retype your number of ordering:");
                Scanner New_Order = new Scanner(System.in);
                Number_of_New_Order = New_Order.nextLine();
                quantity = Integer.parseInt(Number_of_New_Order);
            }

            while (inventoryBalances.hasNext()) {
                Balance b = inventoryBalances.next();
                Boolean x = b.sumBy.get("asset_alias").equals("SurfBoard");
                if (x) {
                    while (quantity > b.amount || quantity<1) {
                        System.out.println("We currently have "+b.amount+" Surfboards in inventory, please rent 1 -- "+b.amount+" Surfboard");
                        Scanner New_Order = new Scanner(System.in);
                        Number_of_New_Order = New_Order.nextLine();
                        quantity = Integer.parseInt(Number_of_New_Order);
                    }
                }
            }

            System.out.println("Thanks " + newAccountName + "!!!" + "\n" + "You will rent : " + quantity + " Surfboard" + ", have a good day!!! ");

            NewAccount_key = MockHsm.Key.create(client);
            HsmSigner.addKey(NewAccount_key, MockHsm.getSignerClient(client));

            NewAccount_Dollar_key = MockHsm.Key.create(client);
            HsmSigner.addKey(NewAccount_Dollar_key, MockHsm.getSignerClient(client));

            NewAccount_Pledge_key = MockHsm.Key.create(client);
            HsmSigner.addKey(NewAccount_Pledge_key, MockHsm.getSignerClient(client));

            new Account.Builder()
                    .setAlias(newAccountName)
                    .addRootXpub(NewAccount_key.xpub)
                    .setQuorum(1)
                    .create(client);

            new Asset.Builder()
                    .setAlias(NewAccount_Dollar)
                    .addRootXpub(NewAccount_Dollar_key.xpub)
                    .setQuorum(1)
                    .create(client);

            new Asset.Builder()
                    .setAlias(NewAccount_Pledge)
                    .addRootXpub(NewAccount_Pledge_key.xpub)
                    .setQuorum(1)
                    .create(client);

            Transaction.Template Money_in_Customer_Wallet = new Transaction.Builder()
                    .addAction(new Transaction.Action.Issue()
                            .setAssetAlias(NewAccount_Dollar)
                            .setAmount(rn)
                    ).addAction(new Transaction.Action.ControlWithAccount()
                            .setAccountAlias(newAccountName)
                            .setAssetAlias(NewAccount_Dollar)
                            .setAmount(rn)
                    ).build(client);


            Transaction.submit(client, HsmSigner.sign(Money_in_Customer_Wallet));

        }

        MockHsm.Key.Items Previouskey= new MockHsm.Key.QueryBuilder().execute(client);

        while(Previouskey.hasNext()){
            MockHsm.Key x=Previouskey.next();
            HsmSigner.addKey(x, MockHsm.getSignerClient(client));
        }

        Transaction.Template Customer_sign_Pledge = new Transaction.Builder()
                .addAction(new Transaction.Action.Issue()
                        .setAssetAlias(NewAccount_Pledge)
                        .setAmount(1)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias(newAccountName)
                        .setAssetAlias(NewAccount_Pledge)
                        .setAmount(1)
                ).build(client);
        Transaction.submit(client, HsmSigner.sign(Customer_sign_Pledge));

        Transaction.Template rent_trade = new Transaction.Builder()
                .addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountAlias(newAccountName)
                        .setAssetAlias(NewAccount_Dollar)
                        .setAmount(15*quantity)
                ).addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountAlias(newAccountName)
                        .setAssetAlias(NewAccount_Pledge)
                        .setAmount(1)
                ).addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountAlias(Surfboard_company)
                        .setAssetAlias(SurfBoard)
                        .setAmount(quantity)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias(Surfboard_company)
                        .setAssetAlias(NewAccount_Dollar)
                        .setAmount(15*quantity)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias(Surfboard_company)
                        .setAssetAlias(NewAccount_Pledge)
                        .setAmount(1)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias(newAccountName)
                        .setAssetAlias(SurfBoard)
                        .setAmount(quantity)
                ).build(client);

        Transaction.Template signedMultiAssetPayment = HsmSigner.sign(rent_trade);

        Transaction.submit(client, signedMultiAssetPayment);

        System.out.println("Thank you very much, you have successfully made the order!!!");

        Boolean In_Rent = true;

        while (In_Rent) {
            System.out.println("Do you want to return the SurfBoard you rented now?");
            System.out.println("Press 1 for return, press 2 for return later");
            System.out.println("Please enter:");
            Scanner Customer_Type = new Scanner(System.in);
            String customer_input = Customer_Type.nextLine();
            if (customer_input.equals("1")) {
                In_Rent = false;

                Transaction.Template return_surfboard = new Transaction.Builder()
                        .addAction(new Transaction.Action.SpendFromAccount()
                                .setAccountAlias(newAccountName)
                                .setAssetAlias(SurfBoard)
                                .setAmount(quantity)
                        ).addAction(new Transaction.Action.SpendFromAccount()
                                .setAccountAlias(Surfboard_company)
                                .setAssetAlias(NewAccount_Pledge)
                                .setAmount(1)
                        ).addAction(new Transaction.Action.ControlWithAccount()
                                .setAccountAlias(Surfboard_company)
                                .setAssetAlias(SurfBoard)
                                .setAmount(quantity)
                        ).addAction(new Transaction.Action.ControlWithAccount()
                                .setAccountAlias(newAccountName)
                                .setAssetAlias(NewAccount_Pledge)
                                .setAmount(1)
                        ).build(client);

                Transaction.submit(client, HsmSigner.sign(return_surfboard));

            }
        }

        Transaction.Template Destroy_Pledge = new Transaction.Builder()
                .addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountAlias(newAccountName)
                        .setAssetAlias(NewAccount_Pledge)
                        .setAmount(1)
                ).addAction(new Transaction.Action.Retire()
                        .setAssetAlias(NewAccount_Pledge)
                        .setAmount(1)
                ).build(client);
        Transaction.submit(client, HsmSigner.sign(Destroy_Pledge));

    }
}
