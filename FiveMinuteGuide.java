import com.chain.api.*;
import com.chain.http.*;
import com.chain.signing.*;

class FiveMinuteGuide {
    public static void main(String[] args) throws Exception {
        // snippet create-client
        Client client = new Client();
        // endsnippet

        // snippet create-key
        MockHsm.Key key = MockHsm.Key.create(client);
        // endsnippet

        // snippet load the key into the HSM Signer, which will communicate with the Mock HSM.
        HsmSigner.addKey(key, MockHsm.getSignerClient(client));
        // endsnippet

        // snippet create asset, gold is asset
        new Asset.Builder()
                .setAlias("gold")
                .addRootXpub(key.xpub)
                .setQuorum(1)
                .create(client);
        // endsnippet

        // snippet create-account-alice
        new Account.Builder()
                .setAlias("alice")
                .addRootXpub(key.xpub)
                .setQuorum(1)
                .create(client);
        // endsnippet

        // snippet create-account-bob
        new Account.Builder()
                .setAlias("bob")
                .addRootXpub(key.xpub)
                .setQuorum(1)
                .create(client);
        // endsnippet

        // snippet create a extra account to create invalid transaction
        new Account.Builder()
                .setAlias("roger")
                .addRootXpub(key.xpub)
                .setQuorum(1)
                .create(client);

        // snippet issue issue the 100 units of the asset Gold to alice account
        Transaction.Template issuance = new Transaction.Builder()
                .addAction(new Transaction.Action.Issue()
                        .setAssetAlias("gold")
                        .setAmount(100)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias("alice")
                        .setAssetAlias("gold")
                        .setAmount(100)
                ).build(client);


        Transaction.submit(client, HsmSigner.sign(issuance));
        // endsnippet

        // Spend more than 100 units, which will leads to a error.
        Transaction.Template spending = new Transaction.Builder()
                .addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountAlias("alice")
                        .setAssetAlias("gold")
                        .setAmount(500))
                .addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias("bob")
                        .setAssetAlias("gold")
                        .setAmount(500)
                ).build(client);

        Transaction.submit(client, HsmSigner.sign(spending));
        // endsnippet

        // snippet retire
        Transaction.Template retirement = new Transaction.Builder()
                .addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountAlias("bob")
                        .setAssetAlias("gold")
                        .setAmount(50)
                ).addAction(new Transaction.Action.Retire()
                        .setAssetAlias("gold")
                        .setAmount(50)
                ).build(client);

        Transaction.submit(client, HsmSigner.sign(retirement));
        // endsnippet
    }
}