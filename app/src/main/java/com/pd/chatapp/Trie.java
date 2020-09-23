package com.pd.chatapp;

import java.util.HashMap;
import java.util.Map;

public class Trie {
    private class TrieNode
    {
        Map<Character,TrieNode> children;

        boolean isEndOfWord;

        TrieNode(){
            isEndOfWord = false;
            children=new HashMap<>();
        }
    };

    private final TrieNode root;
    public Trie() {
        root=new TrieNode();
    }

    public void insert(String key)
    {
        TrieNode current=root;
        for(int i=0;i<key.length();i++)
        {
            char ch=key.charAt(i);
            TrieNode node =current.children.get(ch);
            if(node==null)
            {
                node=new TrieNode();
                current.children.put(ch,node);
            }
            current=node;
        }
        current.isEndOfWord=true;
    }

    public boolean search(String key)
    {
       TrieNode current=root;
       for(int i=0;i<key.length();i++)
       {
           char ch=key.charAt(i);
           TrieNode node=current.children.get(ch);
           if(node==null)return false;

           current=node;
       }
       return current.isEndOfWord;
    }
}
