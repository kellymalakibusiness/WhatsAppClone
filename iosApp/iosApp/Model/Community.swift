//
//  Community.swift
//  iosApp
//
//  Created by Kelly Malaki on 20/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

struct Community {
    let name: String
    let image: String
    let announcement: CommunityAnnouncement
    let groups: [CommunityGroup]
}

struct CommunityGroup {
    let name: String
    let image: String
    let sender: String
    let message: String
    let hasMessage: Bool
    let latestDate: String
}

struct CommunityAnnouncement {
    let latestDate: String
    let sender: String
    let message: String
    let hasUnreadMessage: Bool
}

struct UpdatesChannel {
    let name: String
    let image: String
    let content: String
    let latestDate: String
    let unreadMessages: Int?
}

struct ChannelToFollow {
    let name: String
    let image: String
    let followers: String
    let isVerified: Bool
}
