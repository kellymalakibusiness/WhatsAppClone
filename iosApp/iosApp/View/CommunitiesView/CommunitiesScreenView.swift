//
//  CommunitiesScreenView.swift
//  iosApp
//
//  Created by Kelly Malaki on 12/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct CommunitiesScreenView: View {
    let communities = generateCommunitiesPageDetails()
    @Environment(\.colorScheme) var scheme
    var background: Color {
        if(scheme == .light){
            .white
        } else {
            .black
        }
    }
    var body: some View {
        NavigationStack {
            List {
                Section {
                    Button(action: {}, label: {
                        HStack(spacing: 10) {
                            newCommunityIcon
                            Text("New Community")
                                .foregroundStyle(.primary)
                                .fontWeight(.semibold)
                        }.padding(.vertical, 10)
                    })
                }
                
                ForEach(communities, id: \.name) { community in
                    Section {
                        HStack(spacing: 10) {
                            drawIconImage(image: community.image, size: 40)
                            
                            Text(community.name)
                                .fontWeight(.semibold)
                        }
                        HStack(spacing: 20) {
                            annoucementIcon
                            
                            VStack(spacing: 0) {
                                HStack {
                                    Text("Announcements")
                                        .fontWeight(.semibold)
                                    Spacer()
                                    generateDateComposable(date: community.announcement.latestDate, hasUnreadMessage: community.announcement.hasUnreadMessage)
                                }
                                generateMessageRow(sender: community.announcement.sender, message: community.announcement.message, isUnread: community.announcement.hasUnreadMessage)
                            }
                        }
                        if let firstGroup = community.groups.first {
                            HStack {
                                drawIconImage(image: firstGroup.image, size: 40)
                                VStack(spacing: 0) {
                                    HStack {
                                        Text(firstGroup.name)
                                            .fontWeight(.semibold)
                                        Spacer()
                                        generateDateComposable(date: firstGroup.latestDate, hasUnreadMessage: community.announcement.hasUnreadMessage)
                                    }
                                    
                                    generateMessageRow(sender: firstGroup.sender, message: firstGroup.message, isUnread: firstGroup.hasMessage)
                                }
                            }
                        }
                        
                        //Add the see all navigation link
                        //Divider().listRowSpacing(0).listRowSeparator(.hidden).listRowInsets(.none)
                        NavigationLink(value: 1) {
                            Text("See all")
                                .foregroundStyle(.secondary)
                                .fontWeight(.semibold)
                        }
                    }
                }
            }
            
            
            
            .navigationTitle("Communities")
        }
    }
    
    var newCommunityIcon: some View {
        Image(systemName: "person.3.fill")
            .foregroundColor(background)
            .background {
                RoundedRectangle(cornerSize: CGSize(width: 10, height: 10))
                    .fill(Color.secondary)
                    .frame(width: 40, height: 40)
            }
            .overlay {
                Image(systemName: "plus")
                    .font(.headline)
                    .padding(2)
                    .foregroundColor(background)
                    .background {
                        Circle()
                            .fill(Color("IconGreen"))
                    }
                    .offset(x: 10, y: 10)
            }
    }
    
    var annoucementIcon: some View {
        Image(systemName: "megaphone.fill")
            .foregroundStyle(Color("IconGreen"))
            .background{
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color("IconGreen").opacity(0.3))
                    .frame(width: 40, height: 40)
            }
            .padding(4)
    }
    
    func generateMessageRow(sender: String, message: String, isUnread: Bool) -> HStack<some View> {
        return HStack {
            Text("\(sender):")
                .font(.callout)
                .foregroundStyle(.secondary)
                .fontWeight(.semibold)
            
            Text(message)
                .font(.callout)
                .foregroundStyle(.secondary)
                .lineLimit(1)
            
            Spacer()
            
            if(isUnread){
                Circle()
                    .fill(Color("IconGreen"))
                    .frame(width: 10, height: 10)
            }
        }
    }
}

#Preview {
    CommunitiesScreenView()
}
