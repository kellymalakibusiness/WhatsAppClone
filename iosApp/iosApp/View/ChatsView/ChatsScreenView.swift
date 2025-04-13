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
    @State private var offset: CGFloat = 0
    
    @State private var startMinY: CGFloat = 0
    @State private var searchBarOffset: CGFloat = 0
    
    @State private var showSearchBar: Bool = false
    
    @Environment(\.colorScheme) var scheme
    var background: Color {
        if(scheme == .light){
            .white
        } else {
            .black
        }
    }
    
    let allMessages = generateMessages()
    var body: some View {
        NavigationStack {
            ZStack(alignment: .top) {
                if(showSearchBar){
                    VStack {
                        searchView($searchText)
                        HStack(spacing: 7) {
                            MessageFilters(title: "All", isSelected: true, isIcon: false).onTapGesture {
                                
                            }
                            MessageFilters(title: "Unread", isSelected: false, isIcon: false)
                            MessageFilters(title: "Favourites", isSelected: false, isIcon: false)
                            MessageFilters(title: "Groups", isSelected: false, isIcon: false)
                            MessageFilters(title: "plus", isSelected: false, isIcon: true)
                            Spacer()
                        }.padding(.horizontal)
                    }.offset(y: -searchBarOffset)
                    .zIndex(1)
                }
                
                VStack {
                    List {
                        if(showSearchBar){
                            Rectangle()
                                    .frame(height: 80)
                                    .foregroundColor(background)
                                    .listRowSeparator(.hidden, edges: .top)
                                    .animation(.linear(duration: 1.5), value: showSearchBar)
                        }
                        ContactView(contact: nil)
                            .overlay(content: {
                                GeometryReader{ proxy -> Color in
                                    let minY = proxy.frame(in: .global).minY
                                    DispatchQueue.main.async {
                                        //First we set our offset
                                        if(startMinY == 0){
                                            startMinY = minY
                                        }
                                        
                                        if(showSearchBar){
                                            offset = startMinY - minY
                                            if(offset < 110){
                                                searchBarOffset = offset
                                            }
                                        } else {
                                            //Show searchbar logic
                                            let currentOffset = startMinY - minY
                                            
                                            if(currentOffset < offset && currentOffset < 20){
                                                startMinY = 0
                                                showSearchBar = true
                                            }
                                            
                                        }
                                        
                                    }
                                    
                                    return Color.clear
                                }.frame(height: 0)
                            })
                        
                            .listRowSeparator(.hidden, edges: .top)
                        ForEach(allMessages, id: \.self.title) { contact in
                            ContactView(contact: contact)
                        }
                    }
                    .listStyle(.inset)
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
                }
                .navigationTitle("Chats")
            }
        }
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
            .foregroundColor(background)
            .background {
                Circle()
                    .fill(Color("IconGreen"))
            }
    }
}

#Preview {
    ChatsScreenView()
}
