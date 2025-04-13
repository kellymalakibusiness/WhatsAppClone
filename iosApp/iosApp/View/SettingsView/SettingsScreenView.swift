//
//  SettingsScreenView.swift
//  iosApp
//
//  Created by Kelly Malaki on 12/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct SettingsScreenView: View {
    @State private var searchText: String = ""
    
    @State private var startMinY: CGFloat = 0
    @State private var offset: CGFloat = 0
    
    @Environment(\.colorScheme) var scheme
    var background: Color {
        if(scheme == .light){
            .white
        } else {
            .black
        }
    }
    
    let user: User = User(name: "Malaki", image: "kevin_durant", about: "Nothing is stopping you")
    var body: some View {
        NavigationStack {
            ZStack(alignment: .top) {
                searchView($searchText)
                    .zIndex(1)
                    .offset(y: -offset)
                
                List {
                    Section(
                        header: Rectangle().foregroundStyle(.clear).frame(height: 40)
                            .overlay(content: {
                                GeometryReader{ proxy -> Color in
                                    let minY = proxy.frame(in: .global).minY
                                    DispatchQueue.main.async {
                                        //First we set our offset
                                        if(startMinY == 0){
                                            startMinY = minY
                                        }
                                        offset = startMinY - minY
                                    }
                                    
                                    return Color.clear
                                }.frame(height: 0)
                            })
                    ) {
                        userView.listRowSeparator(.hidden, edges: .bottom)
                        settingsRow(image: "avatar", value: "Avatar")
                    }
                    
                    Section {
                        settingsRow(systemImage: "person.crop.rectangle.stack", value: "Lists")
                        settingsRow(systemImage: "megaphone", value: "Broadcast messages")
                        settingsRow(systemImage: "star", value: "Starred messages")
                        settingsRow(systemImage: "laptopcomputer", value: "Linked devices")
                    }
                    
                    Section {
                        settingsRow(systemImage: "key", value: "Account")
                        settingsRow(systemImage: "lock", value: "Privacy")
                        settingsRow(systemImage: "message", value: "Chats")
                        settingsRow(systemImage: "app.badge", value: "Notifications")
                        settingsRow(systemImage: "arrow.up.arrow.down", value: "Storage and data")
                    }
                    
                    Section {
                        settingsRow(systemImage: "info.circle", value: "Help")
                        settingsRow(systemImage: "person.2", value: "Invite a friend")
                    }
                    
                    Section(
                        header: Text("Also from Meta")
                            .fontWeight(.bold)
                            .textCase(.none)
                    ) {
                        settingsRow(image: "instagram", value: "Open Instagram")
                        settingsRow(image: "facebook", value: "Open Facebook")
                        settingsRow(image: "threads", value: "Open Threads")
                    }
                }
                
                Spacer()
            }
            
            
            
            .navigationTitle("Settings")
        }
    }
    
    var userView: some View {
        VStack {
            HStack {
                drawCircleImage(image: user.image, width: 60)
                    .padding(.trailing, 10)
                
                // Name and about
                VStack(alignment: .leading) {
                    Text(user.name)
                    Text(user.about)
                        .lineLimit(1)
                        .font(.callout)
                        .foregroundStyle(.secondary)
                }
                Spacer(minLength: 10)
                
                //The Barcode thingy
                qrCodeButton
            }
            Divider()
        }
    }
    
    var qrCodeButton: some View {
        Image(systemName: "qrcode")
            .padding(8)
            .background {
                Circle()
                    .fill(.secondary.opacity(0.15))
            }
    }
    
    func settingsRow(systemImage: String, value: String) -> some View {
        return NavigationLink(value: 1) {
            HStack {
                Image(systemName: systemImage)
                Text(value)
                    .padding(.horizontal)
            }
        }
    }
    
    func settingsRow(image: String, value: String) -> some View {
        return NavigationLink(value: 1) {
            HStack {
                Image(image)
                    .resizable()
                    .frame(width: 25, height: 25)
                Text(value)
                    .padding(.horizontal)
            }
        }
    }
}

#Preview {
    SettingsScreenView()
}
