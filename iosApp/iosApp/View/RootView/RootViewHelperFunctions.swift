//
//  RootViewHelperFunctions.swift
//  iosApp
//
//  Created by Kelly Malaki on 12/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

extension View {
    func ourTabView(title: String, systemIconImage: String, tag: Int) -> some View {
        self.tabItem {
            Image(systemName: systemIconImage)
            Text(title)
        }.tag(tag)
    }
    
    func ourTabView(title: String, selectedSystemIconImage: String, unselectedIconImage: String, tag: Int, currentTag: Int) -> some View {
        self.tabItem {
            tag == currentTag ? Image(systemName: selectedSystemIconImage) : Image(unselectedIconImage)
            Text(title)
        }.tag(tag)
    }
    
    func ourTabView(title: String, selectedIconImage: String, unselectedIconImage: String, tag: Int, currentTag: Int) -> some View {
        self.tabItem {
            tag == currentTag ? Image(selectedIconImage) : Image(unselectedIconImage)
            Text(title)
        }.tag(tag)
    }
    
    
}
