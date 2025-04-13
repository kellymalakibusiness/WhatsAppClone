//
//  Status.swift
//  iosApp
//
//  Created by Kelly Malaki on 22/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

struct Status {
    let sender: StatusUser
    let status: String
    let statusText: String
    let isViewed: Bool
    
}

struct StatusUser {
    let name: String
    let image: String
}
