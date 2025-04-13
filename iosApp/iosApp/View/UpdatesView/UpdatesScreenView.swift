//
//  UpdatesScreenView.swift
//  iosApp
//
//  Created by Kelly Malaki on 12/12/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct UpdatesScreenView: View {
    let hardCodedStatus = generateHardCodedStatus()
    let channels = generateHardCodedChannels()
    let channelsToFollow = generateHardCodedChannnelsToFollow()
    @State private var searchText: String = ""
    var body: some View {
        NavigationStack {
            List {
                //Status
                Section {
                    ScrollView(.horizontal, showsIndicators: false) {
                        LazyHStack(content: {
                            ForEach(hardCodedStatus, id: \.sender.name) { status in
                                showStatus(status: status)
                            }
                        })
                    }
                } header: {
                    HStack {
                        Text("Status")
                            .fontWeight(.semibold)
                            .font(.title2)
                        
                        Spacer()
                        cameraButton
                        addTextStatusButton
                    }
                }.listRowSeparator(.hidden, edges: .all)
                
                //Channels
                Section {
                    ForEach(channels, id: \.name) { channel in
                        showChannel(channel: channel)
                    }
                } header: {
                    HStack {
                        Text("Channels")
                            .fontWeight(.semibold)
                            .font(.title3)
                        
                        Spacer()
                        exploreButton
                    }
                }
                
                //Channels to follow
                Section {
                    
                } header: {
                    
                }
            }
            .foregroundColor(.primary)
            .listStyle(.plain)
            
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    toolbarMoreButton
                }
            }
            
            .navigationTitle("Updates")
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
    
    var addTextStatusButton: some View {
        Image(systemName: "pencil")
            .font(.caption)
            .padding(7)
            .background {
                Circle()
                    .fill(.secondary.opacity(0.15))
            }
    }
    
    var exploreButton: some View {
        Text("Explore")
            .font(.caption)
            .fontWeight(.semibold)
            .padding(7)
            .background {
                RoundedRectangle(cornerSize: CGSize(width: 30, height: 30))
                    .fill(.secondary.opacity(0.15))
            }
    }
    
    func showStatus(status: Status) -> some View {
        Image(status.status)
            .resizable()
            .aspectRatio(contentMode: .fill)
            .frame(width: 100, height: 180)
            .cornerRadius(10)
    }
    
    func showChannel(channel: UpdatesChannel) -> some View {
        Button(action: {}, label: {
            HStack {
                drawIconImage(image: channel.image, size: 60)
                VStack(spacing: 0) {
                    HStack {
                        Text(channel.name)
                            .fontWeight(.semibold)
                        Spacer()
                        generateDateComposable(date: channel.latestDate, hasUnreadMessage: channel.unreadMessages != nil)
                    }
                    
                    HStack {
                        Text(channel.content)
                            .font(.callout)
                            .foregroundStyle(.secondary)
                            .lineLimit(1)
                        
                        Spacer()
                        
                        if let unreadMessages = channel.unreadMessages {
                            Circle()
                                .fill(Color("IconGreen"))
                                .frame(width: 10, height: 10)
                        }
                    }
                }
            }
        })
    }
}

#Preview {
    UpdatesScreenView()
}
