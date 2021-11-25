package com.example.my_memory.models

import com.example.my_memory.utils.DEFAULT_ICONS

class Memorygame(private val boardSize: BoardSize){
    val cards: List<Memorycard>
    var numPairsFound = 0

    private var numCardsFlip = 0
    private var indexOfSingleSelectedcard: Int? = null

    init {
        val chosenimage = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedimages = (chosenimage+chosenimage).shuffled()
        cards = randomizedimages.map{Memorycard(it)}
    }

    fun flipCard(postion: Int): Boolean{
        numCardsFlip++
        val cards = cards[postion]
        //0 cards
        //1 cards
        //2 cards
        var foundMatch = false
        if(indexOfSingleSelectedcard == null){
            //0 or 2 cards flipped
            restoreCards()
            indexOfSingleSelectedcard = postion
        }
        else{
            foundMatch = checkforMatch(indexOfSingleSelectedcard!!,postion)
            indexOfSingleSelectedcard = null
        }
        cards.isFaceup = !cards.isFaceup
        return foundMatch
    }

    private fun checkforMatch(postion1: Int, postion2: Int): Boolean {
        if(cards[postion1].identifier != cards[postion2].identifier){
            return false
        }
        cards[postion1].isMatched=true
        cards[postion2].isMatched=true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for(card in cards){
            if(!card.isMatched) {
                card.isFaceup = false
            }
        }
    }


    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFacedUp(postion: Int): Boolean {
        return cards[postion].isFaceup
    }

    fun getnummoves(): Int {
        return numCardsFlip/2
    }

}