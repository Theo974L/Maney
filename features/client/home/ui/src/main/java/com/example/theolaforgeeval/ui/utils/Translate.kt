package com.example.theolaforgeeval.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object Translate {

     fun imageVectorToName(icon: ImageVector): String {
        return when (icon) {
            Icons.Default.DirectionsCar -> "DirectionsCar"
            Icons.Default.Flight -> "Flight"
            Icons.Default.Home -> "Home"
            Icons.Default.Restaurant -> "Restaurant"
            Icons.Default.ShoppingCart -> "ShoppingCart"
            Icons.Default.LocalHospital -> "LocalHospital"
            Icons.Default.Pets -> "Pets"
            Icons.Default.SportsSoccer -> "SportsSoccer"

            Icons.Default.Savings -> "Savings"
            Icons.Default.AttachMoney -> "AttachMoney"
            Icons.Default.AccountBalance -> "AccountBalance"
            Icons.Default.CreditCard -> "CreditCard"

            Icons.Default.School -> "School"
            Icons.Default.Book -> "Book"
            Icons.Default.Work -> "Work"
            Icons.Default.Business -> "Business"

            Icons.Default.Movie -> "Movie"
            Icons.Default.MusicNote -> "MusicNote"
            Icons.Default.SportsEsports -> "SportsEsports"
            Icons.Default.PhotoCamera -> "PhotoCamera"

            Icons.Default.FitnessCenter -> "FitnessCenter"
            Icons.Default.DirectionsBike -> "DirectionsBike"
            Icons.Default.DirectionsBus -> "DirectionsBus"
            Icons.Default.Train -> "Train"

            Icons.Default.PhoneAndroid -> "PhoneAndroid"
            Icons.Default.Computer -> "Computer"
            Icons.Default.Wifi -> "Wifi"
            Icons.Default.Devices -> "Devices"

            Icons.Default.ChildCare -> "ChildCare"
            Icons.Default.FamilyRestroom -> "FamilyRestroom"
            Icons.Default.Cake -> "Cake"
            Icons.Default.LocalCafe -> "LocalCafe"

            Icons.Default.Forest -> "Forest"
            Icons.Default.Park -> "Park"
            Icons.Default.LocalFlorist -> "LocalFlorist"
            Icons.Default.BeachAccess -> "BeachAccess"

            else -> "Help"
        }
    }

    fun iconFromName(name: String): ImageVector {
        return when (name) {

            "DirectionsCar" -> Icons.Default.DirectionsCar
            "Flight" -> Icons.Default.Flight
            "Home" -> Icons.Default.Home
            "Restaurant" -> Icons.Default.Restaurant
            "ShoppingCart" -> Icons.Default.ShoppingCart
            "LocalHospital" -> Icons.Default.LocalHospital
            "Pets" -> Icons.Default.Pets
            "SportsSoccer" -> Icons.Default.SportsSoccer

            "Savings" -> Icons.Default.Savings
            "AttachMoney" -> Icons.Default.AttachMoney
            "AccountBalance" -> Icons.Default.AccountBalance
            "CreditCard" -> Icons.Default.CreditCard

            "School" -> Icons.Default.School
            "Book" -> Icons.Default.Book
            "Work" -> Icons.Default.Work
            "Business" -> Icons.Default.Business

            "Movie" -> Icons.Default.Movie
            "MusicNote" -> Icons.Default.MusicNote
            "SportsEsports" -> Icons.Default.SportsEsports
            "PhotoCamera" -> Icons.Default.PhotoCamera

            "FitnessCenter" -> Icons.Default.FitnessCenter
            "DirectionsBike" -> Icons.Default.DirectionsBike
            "DirectionsBus" -> Icons.Default.DirectionsBus
            "Train" -> Icons.Default.Train

            "PhoneAndroid" -> Icons.Default.PhoneAndroid
            "Computer" -> Icons.Default.Computer
            "Wifi" -> Icons.Default.Wifi
            "Devices" -> Icons.Default.Devices

            "ChildCare" -> Icons.Default.ChildCare
            "FamilyRestroom" -> Icons.Default.FamilyRestroom
            "Cake" -> Icons.Default.Cake
            "LocalCafe" -> Icons.Default.LocalCafe

            "Forest" -> Icons.Default.Forest
            "Park" -> Icons.Default.Park
            "LocalFlorist" -> Icons.Default.LocalFlorist
            "BeachAccess" -> Icons.Default.BeachAccess

            "Add" -> Icons.Default.Add
            "Remove" -> Icons.Default.Remove
            "SwapHoriz" -> Icons.Default.SwapHoriz


            else -> Icons.Default.Help
        }
    }

}