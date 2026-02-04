import React, { useState } from 'react';
import styled from 'styled-components';
import Button from '../components/UI/Button';

const PageContainer = styled.div`
  max-width: 800px;
  margin: 0 auto;
`;

const PageHeader = styled.header`
  margin-bottom: ${({ theme }) => theme.spacing[8]};
  text-align: center;
`;

const PageTitle = styled.h1`
  font-size: ${({ theme }) => theme.fontSizes['4xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const PageDescription = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  color: ${({ theme }) => theme.colors.gray[600]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
`;

const ContactSection = styled.section`
  background: white;
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[8]};
  box-shadow: ${({ theme }) => theme.shadows.md};
  margin-bottom: ${({ theme }) => theme.spacing[8]};
`;

const ContactGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: ${({ theme }) => theme.spacing[8]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.lg}) {
    grid-template-columns: 1fr 1fr;
  }
`;

const ContactInfo = styled.div`
  h2 {
    font-size: ${({ theme }) => theme.fontSizes['2xl']};
    font-weight: ${({ theme }) => theme.fontWeights.bold};
    color: ${({ theme }) => theme.colors.gray[900]};
    margin-bottom: ${({ theme }) => theme.spacing[6]};
  }
`;

const ContactItem = styled.div`
  display: flex;
  align-items: flex-start;
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const ContactIcon = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.xl};
  margin-right: ${({ theme }) => theme.spacing[3]};
  margin-top: ${({ theme }) => theme.spacing[1]};
`;

const ContactDetails = styled.div`
  h3 {
    font-size: ${({ theme }) => theme.fontSizes.lg};
    font-weight: ${({ theme }) => theme.fontWeights.semibold};
    color: ${({ theme }) => theme.colors.gray[900]};
    margin-bottom: ${({ theme }) => theme.spacing[1]};
  }
  
  p {
    color: ${({ theme }) => theme.colors.gray[600]};
    margin: 0;
  }
`;

const ContactForm = styled.form`
  h2 {
    font-size: ${({ theme }) => theme.fontSizes['2xl']};
    font-weight: ${({ theme }) => theme.fontWeights.bold};
    color: ${({ theme }) => theme.colors.gray[900]};
    margin-bottom: ${({ theme }) => theme.spacing[6]};
  }
`;

const FormGroup = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const FormLabel = styled.label`
  display: block;
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[700]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const FormInput = styled.input`
  width: 100%;
  padding: ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
  transition: border-color ${({ theme }) => theme.transitions.fast};
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const FormTextarea = styled.textarea`
  width: 100%;
  padding: ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
  min-height: 120px;
  resize: vertical;
  transition: border-color ${({ theme }) => theme.transitions.fast};
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary[100]};
  }
`;

const OfficeSection = styled.section`
  background: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[8]};
  text-align: center;
`;

const OfficeTitle = styled.h2`
  font-size: ${({ theme }) => theme.fontSizes['2xl']};
  font-weight: ${({ theme }) => theme.fontWeights.bold};
  color: ${({ theme }) => theme.colors.gray[900]};
  margin-bottom: ${({ theme }) => theme.spacing[4]};
`;

const OfficeAddress = styled.div`
  font-size: ${({ theme }) => theme.fontSizes.lg};
  color: ${({ theme }) => theme.colors.gray[600]};
  line-height: ${({ theme }) => theme.lineHeights.relaxed};
  margin-bottom: ${({ theme }) => theme.spacing[6]};
`;

const ContactPage: React.FC = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    subject: '',
    message: ''
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    // Simulate form submission
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // Reset form
    setFormData({
      name: '',
      email: '',
      phone: '',
      subject: '',
      message: ''
    });
    
    setIsSubmitting(false);
    alert('Thank you for your message! We will get back to you soon.');
  };

  return (
    <PageContainer>
      <PageHeader>
        <PageTitle>Contact Us</PageTitle>
        <PageDescription>
          Get in touch with the Telangana Ball Badminton Association. 
          We're here to help with any questions or inquiries you may have.
        </PageDescription>
      </PageHeader>

      <ContactSection>
        <ContactGrid>
          <ContactInfo>
            <h2>Get in Touch</h2>
            
            <ContactItem>
              <ContactIcon>📍</ContactIcon>
              <ContactDetails>
                <h3>Office Address</h3>
                <p>
                  Telangana Ball Badminton Association<br />
                  Sports Complex, Gachibowli<br />
                  Hyderabad, Telangana 500032
                </p>
              </ContactDetails>
            </ContactItem>

            <ContactItem>
              <ContactIcon>📞</ContactIcon>
              <ContactDetails>
                <h3>Phone</h3>
                <p>+91 40 2345 6789</p>
                <p>+91 98765 43210</p>
              </ContactDetails>
            </ContactItem>

            <ContactItem>
              <ContactIcon>✉️</ContactIcon>
              <ContactDetails>
                <h3>Email</h3>
                <p>info@tbba.org</p>
                <p>secretary@tbba.org</p>
              </ContactDetails>
            </ContactItem>

            <ContactItem>
              <ContactIcon>🕒</ContactIcon>
              <ContactDetails>
                <h3>Office Hours</h3>
                <p>Monday - Friday: 9:00 AM - 6:00 PM</p>
                <p>Saturday: 9:00 AM - 2:00 PM</p>
                <p>Sunday: Closed</p>
              </ContactDetails>
            </ContactItem>
          </ContactInfo>

          <ContactForm onSubmit={handleSubmit}>
            <h2>Send us a Message</h2>
            
            <FormGroup>
              <FormLabel htmlFor="name">Full Name *</FormLabel>
              <FormInput
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                required
              />
            </FormGroup>

            <FormGroup>
              <FormLabel htmlFor="email">Email Address *</FormLabel>
              <FormInput
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                required
              />
            </FormGroup>

            <FormGroup>
              <FormLabel htmlFor="phone">Phone Number</FormLabel>
              <FormInput
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleInputChange}
              />
            </FormGroup>

            <FormGroup>
              <FormLabel htmlFor="subject">Subject *</FormLabel>
              <FormInput
                type="text"
                id="subject"
                name="subject"
                value={formData.subject}
                onChange={handleInputChange}
                required
              />
            </FormGroup>

            <FormGroup>
              <FormLabel htmlFor="message">Message *</FormLabel>
              <FormTextarea
                id="message"
                name="message"
                value={formData.message}
                onChange={handleInputChange}
                placeholder="Please describe your inquiry or message..."
                required
              />
            </FormGroup>

            <Button 
              type="submit" 
              fullWidth 
              loading={isSubmitting}
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Sending...' : 'Send Message'}
            </Button>
          </ContactForm>
        </ContactGrid>
      </ContactSection>

      <OfficeSection>
        <OfficeTitle>Visit Our Office</OfficeTitle>
        <OfficeAddress>
          Our office is located in the heart of Hyderabad's sports district. 
          We welcome visitors during office hours and encourage you to schedule 
          an appointment for important meetings.
        </OfficeAddress>
        <Button variant="outline">
          Schedule a Meeting
        </Button>
      </OfficeSection>
    </PageContainer>
  );
};

export default ContactPage;