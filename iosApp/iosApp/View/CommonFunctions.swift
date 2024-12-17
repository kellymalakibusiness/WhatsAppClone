//
//  CommonFunctions.swift
//  iosApp
//
//  Created by Kelly Malaki on 16/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI


func searchView(_ searchText: Binding<String>) -> some View {
    return HStack {
        Image(systemName: "magnifyingglass").foregroundColor(.secondary)
        TextField("Search", text: searchText)
    }.padding(10)
        .background(.secondary.opacity(0.15))
        .cornerRadius(10)
        .padding(.vertical, 5)
        .padding(.horizontal)
}
