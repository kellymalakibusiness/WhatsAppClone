//
//  ChatsScreenView.swift
//  iosApp
//
//  Created by Kelly Malaki on 12/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct ChatsScreenView: View {
    @State private var searchText: String = ""
    var body: some View {
        NavigationStack {
            searchView
            HStack(spacing: 7) {
                MessageFilters(title: "All", isSelected: true, isIcon: false).onTapGesture {
                    
                }
                MessageFilters(title: "Unread", isSelected: false, isIcon: false)
                MessageFilters(title: "Favourites", isSelected: false, isIcon: false)
                MessageFilters(title: "Groups", isSelected: false, isIcon: false)
                MessageFilters(title: "plus", isSelected: false, isIcon: true)
                Spacer()
            }.padding(.horizontal)
            
            List {
                ContantView(contact: nil)
                ForEach(0 ... 4, id: \.self) { _ in
                    ContantView(contact: Contact(image: "kevin_durant", title: "Kevin Durant", lastMessage: "I'm Kevin Durant. You know who I am. Yeah we on the building but I'm tryna take it to the top floor. I guess drama makes it for a good content.😂", lastMessageDate: "Yesterday", unreadMessages: 1))
                }
            }.listStyle(.inset)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        toolbarMoreButton
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        cameraButton
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        greenAddButton
                    }
                }
                .navigationTitle("Chats")
        }
    }

    var searchView: some View {
        HStack {
            Image(systemName: "magnifyingglass").foregroundColor(.secondary)
            TextField("Search", text: $searchText)
        }.padding(10)
            .background(.secondary.opacity(0.15))
            .cornerRadius(15)
            .padding(.vertical, 5)
            .padding(.horizontal)
    }
    
    var toolbarMoreButton: some View {
        Image(systemName: "ellipsis")
            .padding(13)
            .background {
                Circle()
                    .fill(.secondary.opacity(0.15))
            }
    }
    
    var cameraButton: some View {
        Image(systemName: "camera.fill")
            .font(.caption)
            .padding(7)
            .background {
                Circle()
                    .fill(.secondary.opacity(0.15))
            }
    }
    
    var greenAddButton: some View {
        Image(systemName: "plus")
            .font(.caption)
            .padding(7)
            .foregroundColor(Color("WhiteBlackInverse"))
            .background {
                Circle()
                    .fill(Color("IconGreen"))
            }
    }
}

#Preview {
    ChatsScreenView()
}
