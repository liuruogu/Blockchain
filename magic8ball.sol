pragma solidity ^0.4.0;
import "github.com/Arachnid/solidity-stringutils/strings.sol";
contract Magic8Ball{
    
    using strings for *;

    
    string answer;
    uint y;
    function getAnswer(string question) returns (string)
    {
     
     var answers = [
   'Maybe.', 'Certainly not.', 'I hope so.', 'Not in your wildest dreams.',
  'There is a good chance.', 'Quite likely.', 'I think so.', 'I hope not.',
  'I hope so.', 'Never!', 'Fuhgeddaboudit.', 'Ahaha! Really?!?', 'Pfft.',
  'Sorry, bucko.', 'Hell, yes.', 'Hell to the no.', 'The future is bleak.',
  'The future is uncertain.', 'I would rather not say.', 'Who cares?',
  'Possibly.', 'Never, ever, ever.', 'There is a small chance.', 'Yes!'];
  
        y++;
        uint random_number=uint(sha3(y))%(0+20)-0;
        answer = answers[random_number];
        var s = "Your question is:".toSlice().concat(question.toSlice());
        var w = s.toSlice().concat(answer.toSlice());
        
        return (w);
        
    }
}