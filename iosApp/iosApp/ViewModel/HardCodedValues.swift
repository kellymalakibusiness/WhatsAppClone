//
//  HardCodedValues.swift
//  iosApp
//
//  Created by Kelly Malaki on 20/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

func generateMessages() -> [Contact] {
    return [
        Contact(image: "kevin_durant", title: "Kevin Durant", lastMessage: "I'm Kevin Durant. You know who I am😂", lastMessageDate: "Yesterday", unreadMessages: 1),
        Contact(image: "kevin_durant", title: "Groot", lastMessage: "I am Groot", lastMessageDate: "13:13", unreadMessages: 5),
        Contact(image: "kevin_durant", title: "Batman", lastMessage: "Leave Gotham out of this for your own safety", lastMessageDate: "20/12/2024", unreadMessages: nil),
        Contact(image: "kevin_durant", title: "Micheal", lastMessage: "Do we honestly really need monitors. We could just use our psychic powers right?", lastMessageDate: "19/12/2024", unreadMessages: nil),
        Contact(image: "kevin_durant", title: "Kelly", lastMessage: "Are you liking this so far? I created it😁", lastMessageDate: "19/12/2024", unreadMessages: 8),
        Contact(image: "kevin_durant", title: "James Gunn", lastMessage: "Did you guys just see the new Superman trailer?", lastMessageDate: "19/12/2024", unreadMessages: 1),
        Contact(image: "kevin_durant", title: "Joy", lastMessage: "Hi", lastMessageDate: "1/12/2024", unreadMessages: 1),
        Contact(image: "kevin_durant", title: "Franklin", lastMessage: "Lets get that grind", lastMessageDate: "15/11/2024", unreadMessages: nil),
        Contact(image: "kevin_durant", title: "Trevor", lastMessage: "Red trucks rule!!", lastMessageDate: "10/11/2024", unreadMessages: nil),
        Contact(image: "kevin_durant", title: "Carl Johnson", lastMessage: "Ahh, here we go again🤘", lastMessageDate: "1/1/2004", unreadMessages: nil),
    ]
}

func generateCommunitiesPageDetails() -> [Community] {
    return [
        Community(
            name: "One Family",
            image: "kevin_durant",
            announcement: CommunityAnnouncement(latestDate: "01/01/1998", sender: "Mom", message: "Dinner!!", hasUnreadMessage: true),
            groups: [
                CommunityGroup(name: "Vacation Plans", image: "kevin_durant", sender: "Dad", message: "Did the price of gas just go up😭", hasMessage: true, latestDate: "05/10/2005")
            ]
        ),
        Community(
            name: "SwiftUI University",
            image: "kevin_durant",
            announcement: CommunityAnnouncement(latestDate: "20/07/2025", sender: "El Hefe", message: "Yaayy!! I'm El hefe, let's go😂", hasUnreadMessage: false),
            groups: [
                CommunityGroup(name: "Champions", image: "kevin_durant", sender: "Micheal", message: "A monitor is not a peripheral device. Change my mind", hasMessage: true, latestDate: "07/11/2015"),
                CommunityGroup(name: "Monitor", image: "kevin_durant", sender: "Kevin", message: "We need to find and destroy someone by the name of Micheal", hasMessage: true, latestDate: "19/07/2019"),
            ]
        ),
    ]
}

func generateHardCodedStatus() -> [Status] {
    return [
        Status(sender: StatusUser(name: "Kevin Durant", image: "kevin_durant"), status: "kevin_durant", statusText: "I'm Kevin Durant. You know who I am", isViewed: false),
        Status(sender: StatusUser(name: "Durant", image: "kevin_durant"), status: "kevin_durant", statusText: "Another Durant status", isViewed: false),
        Status(sender: StatusUser(name: "Jane Micheals", image: "kevin_durant"), status: "kevin_durant", statusText: "Blah blah blah", isViewed: true),
        Status(sender: StatusUser(name: "Carl Johnson", image: "kevin_durant"), status: "kevin_durant", statusText: "For my culture", isViewed: true),
        Status(sender: StatusUser(name: "Joy", image: "kevin_durant"), status: "kevin_durant", statusText: "I have run out of things to say", isViewed: true)
    ]
}

func generateHardCodedChannels() -> [UpdatesChannel] {
    return [
        UpdatesChannel(name: "Channel1", image: "kevin_durant", content: "Stuff about channel 1.", latestDate: "Yesterday", unreadMessages: nil),
        UpdatesChannel(name: "Channel2", image: "kevin_durant", content: "Some other content on channels", latestDate: "12/12/2024", unreadMessages: 2)
    ]
}

func generateHardCodedChannnelsToFollow() -> [ChannelToFollow] {
    return [
        ChannelToFollow(name: "Liverpool", image: "kevin_durant", followers: "2.3M followers", isVerified: true),
        ChannelToFollow(name: "Arsenal", image: "kevin_durant", followers: "2.4M followers", isVerified: true),
        ChannelToFollow(name: "Chelsea", image: "kevin_durant", followers: "3.5M followers", isVerified: true),
    ]
}
