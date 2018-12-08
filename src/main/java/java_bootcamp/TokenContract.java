package java_bootcamp;

import java_examples.ArtContract;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract{
    public static final String ID = "java_bootcamp.TokenContract";

    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
       //Shape rules
        if (tx.getInputStates().size() !=0)throw new IllegalArgumentException("shouldn't be inputs.");
        if (tx.getOutputStates().size() != 1) throw new IllegalArgumentException("shouldn't be inputs.");
        if (tx.getCommands().size() !=1 )throw new IllegalArgumentException("TokenState should be just one command.");

        //content rules
        ContractState outputState = tx.getOutput(  0);
        if (!(outputState instanceof TokenState)) throw new IllegalArgumentException(" output should be a TokenState.");
        TokenState outTokenState = (TokenState) outputState;
        if(outTokenState.getAmount() <= 0) throw new IllegalArgumentException(" should be at list one outputstate");

        Command<CommandData> command = tx.getCommand(0);
        if(!(command.getValue() instanceof TokenContract.Commands.Issue))
            throw  new IllegalArgumentException("Command should be Issue");
        PublicKey issuePublicKey = outTokenState.getIssuer().getOwningKey();
        if(!(command.getSigners().contains(issuePublicKey)))
            throw  new IllegalArgumentException("Issuer must be sign the issuance");

    }


}