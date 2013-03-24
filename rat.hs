module Main where 

import Prelude

-- class Rational defines a monoid with neutral epsilon, concatenation and star closure
class Monoid a where
    zero     :: a
    (#)      :: a -> a -> a
    star     :: a ->  a
    -- additive closure
    star x   =  x # star x
    -- neutral zero
    x    # zero = x
    zero # x    = x

--- type of transitions : this is a function from a state and a label to a set of states
type Transition a b = a -> b -> [a]

{--
-- type of sets
data Eq a => Set a = EmptySet |
ConsSet a (Set a)

-- constructors
(<<)  :: a -> Set a -> Set a
x << EmptySet           = ConsSet x EmptySet
x << s@(ConsSet y ys)   
| x /= y    = x << ys 
    | otherwise = s

-- set union 
union :: Set a -> Set a -> Set a 
EmptySet     `union` s        = s 
s            `union` EmptySet = s  
ConsSet x xs `union` s        = ConsSet x (xs `union` s) 

-- set intersection
inter :: Set a -> Set a -> Set a 
EmptySet     `inter` s        = s 
s            `inter` EmptySet = s  
ConsSet x xs `inter` s        = ConsSet x (xs `union` s) 
--}

type Terminals a = a -> Bool
type Initials  a = a -> Bool

false = \ x -> False
true  = \ x -> True

-- insertion into an ordered list without duplicates
insert :: Ord a => a -> [a] -> [a]
insert x ys = let ins xs x []   = xs ++ [x]
		  ins xs x (y:ys) | x >  y = ins (xs ++ [y]) x ys   -- continue
				  | x == y = xs ++ y:ys           -- discard x
				  | otherwise = xs ++ [x] ++ y:ys -- foudn x place
	      in
	      ins [] x ys 

-- type of Automata
data (Ord a, Enum a,Ord b, Show b) => Automata a b = Empty | 
						     Automata [a] (Initials a) (Terminals a) [b] (Transition a b) -- assume states are ordered

-- create a new state not already in automata 
-- newState :: Automata a b -> Automata a b
newState Empty                              = Automata [ toEnum 0 ] false false [] (\ x y -> []) 
newState (Automata ss init term lbls trans) = (Automata (ss ++ [succ (last ss)]) init term lbls trans)

-- set a state initial - assumes state is in automata
-- setInit  :: Automata a b -> a -> Automata a b
setInit  Empty                              s  = Automata [s] (\x -> if x == s then True else False) false [] (\ x y -> []) 
setInit (Automata ss init term lbls trans)  s  = Automata ss (\x -> if x == s then True else init x) term lbls trans

-- set a state terminal - assumes state is in automata
-- setTerm  :: Automata a b -> a -> Automata a b
setTerm  Empty                              s  = Automata [s] false (\x -> if x == s then True else False) [] (\ x y -> []) 
setTerm (Automata ss init term lbls trans)  s  = Automata ss init (\x -> if x == s then True else term x)  lbls trans

-- extract all terminal states from automata
-- terminals :: Automata a b -> [a]
terminals Empty                     = []
terminals (Automata ss _ term _ _ ) = filter term ss 

-- extract all initial states from automata
-- initials :: Automata a b -> [a]
initials Empty                    = []
initials (Automata ss init _ _ _) = filter init ss

-- extract all immediate succesors of given state for all labels
---deltaState    :: Automata a b -> a -> [a]
deltaState Empty                   _ = []
deltaState (Automata _ _ _ alph trans) s = concatMap (trans s) alph

-- extract all immediate succesors of given state with given label
-- deltaStLabel    :: Automata a b -> a -> b -> [a]
deltaStLabel Empty                    _ _ = []
deltaStLabel (Automata _ _ _ _ trans) s l = trans s l

-- adds a transition from s1 to s2 using label l-
-- addTrans :: Ord a => Automata a b -> a -> a -> b -> Automata a b
addTrans Empty s1 s2 l = let trans s1 l = [s2] 
			 in 
			 Automata [s1,s2] false false [l] trans 

addTrans (Automata states init term labels trans) s1 s2 l1 
    = (Automata (insert s2 (insert s1 states)) init term (insert l1 labels) (\ s l -> if s == s1 && l == l1 then s2 : (trans s l) else (trans s l)))

-- concatenation of automata
autConcat (Empty) x = x
autConcat x (Empty) = x
autConcat  (Automata states init term labels trans) (Automata states2 init2 term2 labels2 trans2) =
   
 
	  
instance (Ord a, Enum a,Ord b, Show b) => Monoid (Automata a b) where
    zero  = Empty
    -- concatenation
    (#)   = autConcat

instance (Ord a, Enum a,Ord b,Show a, Show b) => Show (Automata a b) where
    show Empty = show "empty"
    show a@(Automata states init term labels trans) = 
        "States =>" ++
	show states ++
	", Start states =" ++
	show (initials a) ++
	", End states =" ++
	show (terminals a) ++
	", Alphabet =" ++
	show (labels) ++
	", Transitions =" ++
	showtrans a
	    where
	    showtrans Empty = "[]"
	    showtrans a@(Automata states init term labels trans) =
		show [ (st,l,end) | st <- states, l <-labels, end <- (trans st l) ]
	

type Auto = Automata Int Char

auto = addTrans (addTrans ((addTrans (newState (newState Empty))) 0 1 'a')  0 0 'b') 1 1 'c'

main = print  auto




