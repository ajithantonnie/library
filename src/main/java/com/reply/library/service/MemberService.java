package com.reply.library.service;

import com.reply.library.dto.MemberDTO;
import com.reply.library.entity.Member;
import com.reply.library.exception.ResourceNotFoundException;
import com.reply.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    
    @Autowired
    private MemberRepository memberRepository;
    
    public MemberDTO createMember(MemberDTO memberDTO) {
        Member member = new Member(memberDTO.getName(), memberDTO.getEmail());
        Member savedMember = memberRepository.save(member);
        return convertToDTO(savedMember);
    }
    
    public Page<MemberDTO> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    public MemberDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return convertToDTO(member);
    }
    
    public MemberDTO updateMember(Long id, MemberDTO memberDTO) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        
        member.setName(memberDTO.getName());
        member.setEmail(memberDTO.getEmail());
        
        Member updatedMember = memberRepository.save(member);
        return convertToDTO(updatedMember);
    }
    
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return memberRepository.existsById(id);
    }
    
    private MemberDTO convertToDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        return dto;
    }
}