import { api, ApiResponse, PaginatedResponse } from './api';
import { Member } from '../store/slices/membersSlice';

export interface MemberFilters {
  position?: string;
  isActive?: boolean;
  search?: string;
}

export interface CreateMemberRequest {
  name: string;
  position: string;
  email: string;
  phone: string;
  biography: string;
  photoUrl?: string;
  hierarchyLevel: number;
  tenureStartDate?: string;
  tenureEndDate?: string;
  isActive: boolean;
  isProminent: boolean;
}

export interface UpdateMemberRequest extends Partial<CreateMemberRequest> {
  id: string;
}

export interface ContactFormRequest {
  memberId: string;
  senderName: string;
  senderEmail: string;
  senderPhone?: string;
  subject: string;
  message: string;
}

export const membersService = {
  getMembers: async (filters?: MemberFilters): Promise<Member[]> => {
    const response = await api.get<Member[]>('/members', { params: filters });
    return response.data.data;
  },

  getMemberById: async (id: string): Promise<Member> => {
    const response = await api.get<Member>(`/members/${id}`);
    return response.data.data;
  },

  createMember: async (memberData: CreateMemberRequest): Promise<Member> => {
    const response = await api.post<Member>('/members', memberData);
    return response.data.data;
  },

  updateMember: async (memberData: UpdateMemberRequest): Promise<Member> => {
    const { id, ...data } = memberData;
    const response = await api.put<Member>(`/members/${id}`, data);
    return response.data.data;
  },

  deleteMember: async (id: string): Promise<void> => {
    await api.delete(`/members/${id}`);
  },

  uploadMemberPhoto: async (id: string, file: File): Promise<string> => {
    const formData = new FormData();
    formData.append('photo', file);
    
    const response = await api.post<{ photoUrl: string }>(`/members/${id}/photo`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    
    return response.data.data.photoUrl;
  },

  getActiveMembers: async (): Promise<Member[]> => {
    const response = await api.get<Member[]>('/members/active');
    return response.data.data;
  },

  getMemberHierarchy: async (): Promise<Member[]> => {
    const response = await api.get<Member[]>('/members/hierarchy');
    return response.data.data;
  },

  sendContactForm: async (contactData: ContactFormRequest): Promise<void> => {
    await api.post('/members/contact', contactData);
  },
};